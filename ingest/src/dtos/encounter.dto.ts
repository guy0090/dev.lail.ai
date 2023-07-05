/**
 * Data helper classes for uploads.
 *
 * Contains validation classes for uploads received from the client.
 *
 * Types are initially taken from https://github.com/lost-ark-dev/meter-core
 * and slightly modified to fit the needs of this project.
 */

import { Zone, determineZone } from '@/game/zones';
import { logger } from '@/utils/logger';
import { MaxSkills, ValidCurrentHp, ValidDeathTime, ValidDeaths, ValidEntities, ValidGearScore } from '@/utils/validators';
import { Type } from 'class-transformer';
import {
  ArrayMaxSize,
  IsArray,
  IsBoolean,
  IsEmpty,
  IsEnum,
  IsIn,
  IsNumber,
  IsObject,
  IsOptional,
  IsString,
  Length,
  ValidateNested,
} from 'class-validator';

export enum StatusEffectTarget {
  OTHER = 0,
  PARTY = 1,
  SELF = 2,
}

// Not actively validated but used for typing
export enum StatusEffectBuffTypeFlags {
  // misc. flags found in logs while testing
  UNK3 = 3,
  UNK7 = 7,
  UNK9 = 9,
  UNK12 = 12,
  UNK14 = 14,
  UNK24 = 24,
  UNK34 = 34,
  UNK72 = 72,
  UNK129 = 129,
  UNK140 = 140,
  UNK192 = 192,
  UNK193 = 193,
  // ---
  NONE = 0,
  DMG = 1,
  CRIT = 2,
  ATKSPEED = 4,
  MOVESPEED = 8,
  HP = 16,
  DEFENSE = 32,
  RESOURCE = 64,
  COOLDOWN = 128,
  STAGGER = 256,
  SHIELD = 512,
  ANY = 262144,
}

export class EffectSkill {
  @IsNumber()
  id: number;

  @IsNumber()
  classid: number;

  @IsString()
  @Length(0, 50)
  icon: string;

  @IsOptional()
  @IsNumber({}, { each: true })
  @ArrayMaxSize(100)
  summonids?: number[];

  @IsOptional()
  @IsNumber()
  summonsourceskill?: number;

  @IsOptional()
  @IsNumber()
  sourceskill?: number;
}

export class StatusEffectSource {
  @IsString()
  @Length(0, 50)
  icon: string;

  @IsOptional()
  @IsString()
  @Length(0, 50)
  setname?: string;

  @IsOptional()
  @Type(() => EffectSkill)
  skill?: EffectSkill;
}

export class StatusEffect {
  @IsNumber()
  id: number;

  @IsEnum(StatusEffectTarget)
  target: StatusEffectTarget;

  @IsString()
  @IsIn(['buff', 'debuff'])
  category: 'buff' | 'debuff';

  @IsString()
  buffcategory: string;

  // @IsEnum(StatusEffectBuffTypeFlags)
  @IsNumber()
  bufftype: StatusEffectBuffTypeFlags;

  @IsNumber()
  uniquegroup: number;

  @ValidateNested()
  @Type(() => StatusEffectSource)
  source: StatusEffectSource;
}

export class Breakdown {
  @IsNumber()
  timestamp: number;

  @IsNumber()
  damage: number;

  @IsString()
  targetEntity: string;

  @IsBoolean()
  isCrit: boolean;

  @IsBoolean()
  isBackAttack: boolean;

  @IsBoolean()
  isFrontAttack: boolean;

  @IsBoolean()
  isBuffedBySupport: boolean;

  @IsBoolean()
  isDebuffedBySupport: boolean;

  @IsArray()
  @IsNumber({}, { each: true })
  @ArrayMaxSize(100)
  debuffedBy: number[];

  @IsArray()
  @IsNumber({}, { each: true })
  @ArrayMaxSize(100)
  buffedBy: number[];
}

export class Hits {
  @IsNumber()
  casts: number;

  @IsNumber()
  total: number;

  @IsNumber()
  crit: number;

  @IsNumber()
  backAttack: number;

  @IsNumber()
  totalBackAttack: number;

  @IsNumber()
  frontAttack: number;

  @IsNumber()
  totalFrontAttack: number;

  @IsNumber()
  counter: number;

  @IsNumber()
  hitsDebuffedBySupport: number;

  @IsNumber()
  hitsBuffedBySupport: number;

  @IsObject()
  hitsBuffedBy: { [key: string]: number };

  @IsObject()
  hitsDebuffedBy: { [key: string]: number };
}

export class Skill {
  @IsNumber()
  id: number;

  @IsOptional()
  @IsString()
  @Length(0, 50)
  icon?: string;

  @IsNumber()
  damageDealt: number;

  @IsNumber()
  damageDealtDebuffedBySupport: number;

  @IsNumber()
  damageDealtBuffedBySupport: number;

  @IsNumber()
  maxDamage: number;

  @ValidateNested()
  @Type(() => Hits)
  hits: Hits;

  // TODO: Set a max value?
  @ValidateNested({ each: true })
  @IsArray()
  @Type(() => Breakdown)
  breakdown: Breakdown[];

  @IsObject()
  damageDealtDebuffedBy: { key: number };

  @IsObject()
  damageDealtBuffedBy: { key: number };
}

export class Entity {
  @IsNumber()
  lastUpdate: number;

  @IsString()
  @Length(1, 12)
  id: string;

  @IsNumber()
  npcId: number;

  @IsOptional()
  @IsString()
  @Length(3, 16)
  name: string;

  @IsOptional()
  @IsEmpty()
  userId?: string; // Set by post-processing

  @IsNumber()
  classId: number;

  @IsOptional()
  @IsString()
  @Length(2, 12) // Have only seen 7 or 8?
  partyId: string;

  @IsBoolean()
  isBoss: boolean;

  @IsBoolean()
  isPlayer: boolean;

  @IsOptional()
  @IsBoolean()
  isEsther: boolean;

  @IsOptional()
  @IsString()
  @Length(0, 50)
  icon?: string;

  @IsBoolean()
  isDead: boolean;

  @ValidDeaths()
  @IsNumber()
  deaths: number;

  @ValidDeathTime()
  @IsNumber()
  deathTime: number;

  @ValidGearScore()
  @IsNumber()
  gearScore: number;

  @ValidCurrentHp()
  @IsNumber()
  currentHp: number;

  @IsNumber()
  maxHp: number;

  @IsNumber()
  damageDealt: number;

  @IsNumber()
  damageDealtDebuffedBySupport: number;

  @IsNumber()
  damageDealtBuffedBySupport: number;

  @IsNumber()
  healingDone: number;

  @IsNumber()
  shieldDone: number;

  @IsNumber()
  damageTaken: number;

  @IsNumber()
  shieldReceived: number;

  @IsNumber()
  damagePreventedWithShieldOnOthers: number;

  @IsNumber()
  damagePreventedByShield: number;

  @MaxSkills({ message: 'Too many skills' })
  @ValidateNested({ each: true })
  @Type(() => Skill)
  skills: Skill[];

  @ValidateNested()
  @Type(() => Hits)
  hits: Hits;

  @IsObject()
  damageDealtDebuffedBy: { [id: string]: number };

  @IsObject()
  damageDealtBuffedBy: { [id: string]: number };

  @IsObject()
  damagePreventedWithShieldOnOthersBy: { [id: string]: number };

  @IsObject()
  damagePreventedByShieldBy: { [id: string]: number };

  @IsObject()
  shieldDoneBy: { [id: string]: number };

  @IsObject()
  shieldReceivedBy: { [id: string]: number };

  isNonTyped() {
    return !this.isBoss && !this.isPlayer && !this.isEsther;
  }
}

export class DamageStatistics {
  @IsNumber()
  totalDamageDealt: number;

  @IsNumber()
  topDamageDealt: number;

  @IsNumber()
  totalDamageTaken: number;

  @IsNumber()
  topDamageTaken: number;

  @IsNumber()
  totalHealingDone: number;

  @IsNumber()
  topHealingDone: number;

  @IsNumber()
  totalShieldDone: number;

  @IsNumber()
  topShieldDone: number;

  @ValidateNested({ each: true })
  @IsArray()
  @Type(() => StatusEffect)
  @ArrayMaxSize(200)
  debuffs: StatusEffect[];

  @ValidateNested({ each: true })
  @IsArray()
  @Type(() => StatusEffect)
  @ArrayMaxSize(200)
  buffs: StatusEffect[];

  @IsNumber()
  topShieldGotten: number;

  @IsNumber()
  totalEffectiveShieldingDone: number;

  @IsNumber()
  topEffectiveShieldingDone: number;

  @IsNumber()
  topEffectiveShieldingUsed: number;

  @ValidateNested({ each: true })
  @IsArray()
  @Type(() => StatusEffect)
  effectiveShieldingBuffs: StatusEffect[];

  @ValidateNested({ each: true })
  @IsArray()
  @Type(() => StatusEffect)
  appliedShieldingBuffs: StatusEffect[];
}

export class UploadDto {
  @IsNumber()
  startedOn: number;

  @IsNumber()
  lastCombatPacket: number;

  @IsNumber()
  fightStartedOn: number;

  @IsString()
  @Length(1, 12)
  currentBoss: string;

  @IsString()
  @Length(1, 12)
  localPlayer: string;

  @ValidEntities({ message: 'Entities failed to be validated' })
  @ValidateNested({ each: true })
  @Type(() => Entity)
  entities: Entity[];

  @ValidateNested()
  @Type(() => DamageStatistics)
  damageStatistics: DamageStatistics;

  /**
   * Process the initially validated data and attempt to determine the zone.
   *
   * - Remove any invalid or irrelevant entities
   * - TODO: ?
   */
  postProcess(): Zone | undefined {
    const zone = this.findZone();
    if (!zone) return undefined;

    if (this.processEntities(zone)) {
      return zone;
    }

    return undefined;
  }

  /**
   * Determine the zone based on the last boss that was updated
   */
  private findZone() {
    const mostRecent: { lastUpdate: number; zone: Zone } = {
      lastUpdate: 0,
      zone: undefined,
    };

    for (const entity of this.entities) {
      if (!entity.isBoss) continue;

      const zone = determineZone(entity.npcId);
      if (zone && mostRecent.lastUpdate < entity.lastUpdate) {
        mostRecent.lastUpdate = entity.lastUpdate;
        mostRecent.zone = zone;
      }
    }

    return mostRecent.zone;
  }

  /**
   * Remove any invalid or irrelevant entities
   *
   * - Non typed entities (not a boss, player or esther)
   * - Players that haven't cast at least 8 skills (probably residual entities from before entering the dungeon)
   *    - TODO: Might need to adapt this depending on if we plan to support bussing, i.e.: check for death time compared to duration of the fight or something
   * - Entities that are not bosses in the current zone
   *
   * If more than one player exists, they must all have a party ID. If not, the upload is ignored.
   *
   * @returns true if the upload should be accepted, false if it should be ignored
   */
  private processEntities(zone: Zone): boolean {
    const validBosses = zone.bosses.map(boss => boss.id);
    let players = 0;
    let bosses = 0;
    let partyIdCount = 0;

    for (let i = this.entities.length - 1; i >= 0; i--) {
      const entity = this.entities[i];

      if (entity.isNonTyped()) {
        logger.debug(`Removing non typed entity:: [${zone.name}] >> ${entity.npcId} (${entity.currentHp}/${entity.maxHp})})`);
        this.entities.splice(i, 1);
      } else if (entity.isPlayer && entity.skills.length < 8) {
        logger.debug(`Removing player with less than 8 skills:: [${zone.name}] >> ${entity.name} (${entity.classId})`);
        this.entities.splice(i, 1);
      } else if (entity.isBoss && !validBosses.includes(entity.npcId)) {
        logger.debug(`Removing boss that is not in the current zone:: [${zone.name}] >> ${entity.npcId} (${entity.currentHp}/${entity.maxHp})})`);
        this.entities.splice(i, 1);
      } else {
        if (entity.isPlayer) {
          players++;
          if (entity.partyId) partyIdCount++;
        } else if (entity.isBoss) {
          bosses++;
          entity.skills = []; // FIXME: Do we need boss skills?
        }
      }
    }

    if (players > 1 && players !== partyIdCount) {
      logger.debug(
        `Rejecting upload '${this.currentBoss}' - '${this.localPlayer}' >> Party ID mismatch. Players: ${players}, Party IDs: ${partyIdCount}}`,
      );
      return false;
    }
    if (players < zone.minPlayers || players > zone.maxPlayers) {
      logger.debug(
        `Rejecting upload ${this.currentBoss} - ${this.localPlayer} >> Player count mismatch. Players: ${players}}, Zone: ${zone.minPlayers}/${zone.maxPlayers}} `,
      );
      return false;
    }
    if (bosses > zone.bosses.length) {
      logger.debug(
        `Rejecting upload ${this.currentBoss} - ${this.localPlayer} >> Boss count mismatch. Bosses: ${bosses}}, Zone Max: ${zone.bosses.length}} `,
      );
      return false;
    }

    return true;
  }

  public assignUserId(userId: string, entityId: string) {
    for (const entity of this.entities) {
      if (entity.id === entityId) {
        entity.userId = userId;
        return;
      }
    }
  }
}
