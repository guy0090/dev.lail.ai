import ms from 'ms'
import { defineStore } from 'pinia'
import { encounterDb } from '../config/LocalDb'
import { utilStore } from './Util'
import type { EncounterSummaryDto } from '@/api/common/dto/ResponseDtos'
import { EncounterStoreEntry } from '@/common/StoreEntries'
import { getEncounterEntry } from '@/api/http/Api'
import type PrivacySetting from '@/api/common/enums/PrivacySetting'
import type { EncounterEntity, EntitySkill } from '@/api/common/interfaces/Encounters'

export const encounterStore = defineStore('encounterStore', {
  state: () => {
    return {
      /**
       * Recent encounters are stored in a separate array. This is used to
       * display the last 24 hours of encounters in the encounters page.
       *
       * Is not persisted in IndexedDB.
       */
      recentEncounters: [] as EncounterSummaryDto[],

      /**
       * Top encounter is stored separately. This is used to display the top
       * encounter in the encounters page.
       *
       * Is not persisted in IndexedDB.
       */
      topEncounter: undefined as EncounterSummaryDto | undefined,

      /**
       * Max TTL is used to determine how long an encounter should be stored before
       * it is removed from the local DB or refreshed from the server.
       */
      maxTtlMs: ms('7d') as number,

      /**
       * Max cache size is used to determine how many encounters should be stored
       * in the local DB before the oldest encounter is removed. Entry key order is
       * same as insertion order, so the oldest encounter is the first key.
       */
      maxCacheSize: 20 as number
    }
  },

  getters: {
    getRecentEncounters(state) {
      return state.recentEncounters
    },

    getTopEncounter(state) {
      return state.topEncounter
    },

    getMaxTtl(state) {
      return state.maxTtlMs
    }
  },

  actions: {
    async getFirstEntry() {
      const encounters = await encounterDb.keys()
      if (encounters.length === 0) return undefined
      return encounters[0]
    },

    async expireOldestEntry() {
      const encounters = await encounterDb.keys()
      if (encounters.length <= this.maxCacheSize) return
      await encounterDb.removeItem(encounters[0])
    },

    async getEncounterEntry(id: string, privacy?: PrivacySetting[]): Promise<EncounterStoreEntry> {
      let entry: EncounterStoreEntry | null = null
      const encounter = await encounterDb.getItem<Uint8Array>(id)
      if (encounter) entry = EncounterStoreEntry.fromEntry(encounter)
      if (entry && !this.isOutdated(entry.lastUpdated)) return entry

      const utilState = utilStore()
      const response = await getEncounterEntry(id, utilState.showEncounterPlayerNames, privacy)
      entry = EncounterStoreEntry.fromApiDto(response)

      this.expireOldestEntry().catch((e) => console.error('Failed to expire oldest entry:', e))
      this.saveEntry(entry).catch((e) => console.error('Failed to save entry:', e))

      return entry
    },

    async saveEntry(entry: EncounterStoreEntry) {
      try {
        await encounterDb.setItem(entry.summary.id, entry.toBuffer())
      } catch (e) {
        console.error('Failed to save encounter to local DB:', e)
      }
    },

    async deleteEntry(id: string) {
      try {
        await encounterDb.removeItem(id)
      } catch (e) {
        console.error('Failed to delete encounter from local DB:', e)
      }
    },

    async clearDb() {
      try {
        await encounterDb.clear()
      } catch (e) {
        console.error('Failed to clear local DB:', e)
      }
    },

    setRecentEncounters(encounters: EncounterSummaryDto[]) {
      this.recentEncounters = encounters
    },

    addRecentEncounters(encounters: EncounterSummaryDto[]) {
      this.recentEncounters.push(...encounters)
    },

    removeRecentEncounters(encounters: EncounterSummaryDto[]) {
      this.recentEncounters = this.recentEncounters.filter((e) => !encounters.includes(e))
    },

    setTopEncounter(encounter?: EncounterSummaryDto) {
      if (!encounter) return
      this.topEncounter = encounter
    },

    isOutdated(lastUpdated: number) {
      return Date.now() - lastUpdated > this.maxTtlMs
    }
  }
})

export const currentEncounterStore = defineStore('currentEncounterStore', {
  state: () => {
    return {
      /**
       * The encounter that is currently being (or was last) viewed.
       *
       * Used for reactively updating the encounter page whithout passing props to child
       * components within the page.
       */
      current: undefined as EncounterStoreEntry | undefined,

      /**
       * Duration start position for all calculated encounter data.
       */
      start: 0 as number,

      /**
       * End time position for all calculated encounter data.
       */
      end: 0 as number
    }
  },

  getters: {
    getCurrent(state) {
      return state.current
    },

    getEncounter(state) {
      return state.current?.encounter
    },

    getSummary(state) {
      return state.current?.summary
    },

    getRelated(state) {
      return state.current?.related
    },

    /**
     * The duration of the encounter as defined by the user.
     */
    getDuration(state) {
      return state.end - state.start
    },

    getStart(state) {
      return state.start
    },

    getEnd(state) {
      return state.end
    },

    getEncounterStart(state) {
      return state.current?.encounter.fightStartedOn ?? 0
    },

    getEncounterEnd(state) {
      return state.current?.encounter.lastCombatPacket ?? 0
    },

    /**
     * The duration of the actual encounter.
     */
    getEncounterDuration(state) {
      return state.current?.encounter.getDuration() ?? 0
    }
  },

  actions: {
    setEncounter(entry: EncounterStoreEntry) {
      this.current = entry
      this.setRanges(entry.encounter.fightStartedOn, entry.encounter.lastCombatPacket)
    },

    clearEncounter() {
      this.current = undefined
    },

    setRanges(start: number, end: number) {
      if (!this.current) return
      this.setStart(start)
      this.setEnd(end)
    },

    /**
     * Set the start time for the current encounter.
     *
     * If the start time is smaller than the encounter start time, the encounter start time is used.
     * @param start The start time in ms.
     */
    setStart(start: number) {
      if (!this.getEncounter || start === this.start) return
      if (start < this.getEncounter.fightStartedOn) this.start = this.getEncounter.fightStartedOn
      else this.start = start
    },

    /**
     * Set the end time for the current encounter.
     *
     * If the end time is larger than the encounter end time, the encounter end time is used.
     * @param end The end time in ms.
     */
    setEnd(end: number) {
      if (!this.getEncounter || end === this.end) return
      if (end > this.getEncounter.lastCombatPacket) this.end = this.getEncounter.lastCombatPacket
      else this.end = end
    },

    /**
     * Check if a given time is within the current encounter range.
     */
    isWithinRange(time: number) {
      return time >= this.start && time <= this.end
    },

    /**
     * Get a list of all players in the current encounter.
     */
    getPlayers() {
      if (!this.getEncounter) return []
      return this.getEncounter.entities.filter((entity) => entity.isPlayer)
    },

    /**
     * Get a list of all bosses in the current encounter.
     */
    getBosses() {
      if (!this.getEncounter) return []
      return this.getEncounter.entities.filter((entity) => entity.isBoss)
    },

    /**
     * Get a list of all sidereals in the current encounter.
     *
     * Function name uses Esthers as thats what the meter calls them.
     */
    getEsthers() {
      if (!this.getEncounter) return []
      return this.getEncounter.entities.filter((entity) => entity.isEsther)
    },

    /**
     * Get a summary of parties in the current encounter.
     * @returns A map of party IDs to the participants in that party sorted by damage (DPS).
     */
    getParties() {
      const parties = new Map<string, EncounterEntity[]>()
      if (!this.getSummary) return parties

      const players = this.getPlayers().reduce(
        (acc, player) => acc.set(player.id, player),
        new Map<string, EncounterEntity>())

      const summary = this.getSummary
      if (summary.participants.length === 4) {
        parties.set('0', this.getPlayers())
      } else {
        summary.participants.forEach((participant) => {
          if (!participant.partyId) return

          const party = parties.get(participant.partyId)
          if (!party) {
            parties.set(participant.partyId, [players.get(participant.id)!])
          } else {
            party.push(players.get(participant.id)!)
          }
        })
      }

      parties.forEach((p) => p.sort((a, b) => b.damageDealt - a.damageDealt))
      return parties
    },

    /**
     * Get the total amount of times players in the encounter died.
     */
    getTotalDeaths() {
      if (!this.getEncounter) return 0
      const encounter = this.getEncounter
      return encounter.entities.reduce((acc, participant) => {
        return acc + (participant.isPlayer ? participant.deaths : 0)
      }, 0)
    },

    /**
     * Get the total damage dealt by a given entity.
     *
     * @param entity The entity to get the damage of.
     * @param useBreakdowns Wether or not to use the breakdowns for the damage calculation.
     * @param rounded Wether or not to round the damage.
     * @returns The total damage dealt by the entity.
     */
    getEntityDamage(entity: EncounterEntity, useBreakdowns = true, rounded = true) {
      if (!this.current) return 0

      let totalDamage = 0.0
      if (useBreakdowns) {
        totalDamage = entity.skills.reduce((accSkill, skill) => {
          const damage =
            skill.breakdown.length === 0
              ? skill.damageDealt
              : skill.breakdown.reduce((accBreakdown, breakdown) => {
                  if (this.isWithinRange(breakdown.timestamp))
                    return accBreakdown + breakdown.damage
                  return accBreakdown + 0.0
                }, 0.0)
          return accSkill + damage
        }, 0.0)
      } else {
        totalDamage += entity.damageDealt
      }

      return rounded ? Math.round(totalDamage) : totalDamage
    },

    /**
     * Get the DPS for an entity in the current encounter.
     *
     * Will calculate DPS based on skills cast between the `start` and `end` time
     * of the encounter. If `useBreakdowns` is set to false, the calculation will
     * not consider `start` and `end` time, and will instead calculate DPS based on
     * the `damageDealt` field on the entity.
     *
     * @param entity The entity to get DPS for.
     * @param useBreakdowns Wether or not to use skill breakdowns to calculate DPS.
     * @param rounded Wether or not to round the DPS value.
     * @returns DPS value for the given entity.
     */
    getEntityDps(entity: EncounterEntity, useBreakdowns = true, rounded = true) {
      if (!this.current) return 0

      const duration = useBreakdowns ? this.getDuration : this.getEncounterDuration
      const totalDamage = this.getEntityDamage(entity, useBreakdowns, false)
      if (duration === 0) return 0

      const dps = totalDamage / (duration / 1000)
      return rounded ? Math.round(dps) : dps
    },

    getTotalDamageDealt(useBreakdowns = true, rounded = true) {
      if (!this.current) return { total: 0, esther: 0, player: 0, averagePlayer: 0 }

      const players = this.getPlayers()
      const esthers = this.getEsthers()

      const estherDamage = esthers.reduce((acc, esther) => {
        return acc + this.getEntityDamage(esther, useBreakdowns, rounded)
      }, 0)

      const playerDamage = players.reduce((acc, player) => {
        return acc + this.getEntityDamage(player, useBreakdowns, rounded)
      }, 0)

      const averageEsther = estherDamage / esthers.length
      const averagePlayer = playerDamage / players.length

      return {
        total: estherDamage + playerDamage,
        esther: estherDamage,
        player: playerDamage,
        averageEsther: rounded ? Math.round(averageEsther) : averageEsther,
        averagePlayer: rounded ? Math.round(averagePlayer) : averagePlayer
      }
    },

    /**
     * Get the total DPS of all entities in the current encounter.
     *
     * Split into esther, player, total and average DPS.
     *
     * @param useBreakdowns Wether or not to use skill breakdowns to calculate DPS.
     * @param rounded Wether or not to round the DPS value.
     * @returns Total DPS of all entities for the current encounter.
     */
    getTotalDps(useBreakdowns = true, rounded = true) {
      if (!this.current)
        return { total: 0, esthers: 0, players: 0, averageEsther: 0, averagePlayer: 0 }
      const players = this.getPlayers()
      const esthers = this.getEsthers()

      const estherDps = esthers.reduce((acc, esther) => {
        return acc + this.getEntityDps(esther, useBreakdowns, rounded)
      }, 0.0)

      const playerDps = players.reduce((acc, player) => {
        return acc + this.getEntityDps(player, useBreakdowns, rounded)
      }, 0.0)

      const averagePlayerDps = playerDps / players.length
      const averageEstherDps = estherDps / esthers.length

      return {
        total: estherDps + playerDps,
        esther: estherDps,
        player: playerDps,
        averageEsther: rounded ? Math.round(averageEstherDps) : averageEstherDps,
        averagePlayer: rounded ? Math.round(averagePlayerDps) : averagePlayerDps
      }
    },

    getPartyDamage(players: EncounterEntity[], useBreakdowns = true, rounded = true) {
      if (!this.current) return 0

      return players.reduce((acc, player) => {
        return acc + this.getEntityDamage(player, useBreakdowns, rounded)
      }, 0)
    },

    getPartyDps(players: EncounterEntity[], useBreakdowns = true, rounded = true) {
      if (!this.current) return 0

      return players.reduce((acc, player) => {
        return acc + this.getEntityDps(player, useBreakdowns, rounded)
      }, 0.0)
    },

    getSkillDamage(skill: EntitySkill, useBreakdowns = true, rounded = true) {
      if (!this.current) return 0

      let totalDamage = 0.0
      if (useBreakdowns) {
        totalDamage = skill.breakdown.reduce((accBreakdown, breakdown) => {
          if (this.isWithinRange(breakdown.timestamp)) return accBreakdown + breakdown.damage
          return accBreakdown + 0.0
        }, 0.0)
      } else {
        totalDamage = skill.damageDealt
      }

      return rounded ? Math.round(totalDamage) : totalDamage
    },

    getSkillDps(skill: EntitySkill, useBreakdowns = true, rounded = true) {
      if (!this.current) return 0

      const duration = useBreakdowns ? this.getDuration : this.getEncounterDuration
      const totalDamage = this.getSkillDamage(skill, useBreakdowns, false)
      if (duration === 0) return 0

      const dps = totalDamage / (duration / 1000)
      return rounded ? Math.round(dps) : dps
    },

    /**
     * Get participants that have an associated app userId.
     */
    getUserParticipants() {
      const summary = this.getSummary
      if (!summary) return []
      return summary.participants.filter((participant) => participant.userId)
    }
  }
})
