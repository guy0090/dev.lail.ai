import type { EncounterSummaryDto, UserDto, UserSelfDto } from '@/api/common/dto/ResponseDtos'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import durations from 'dayjs/plugin/duration'
import { classColors } from './Game'
import type { EncounterParty } from '@/api/common/interfaces/Encounters'
import { ApiException } from '@/api/http/ApiException'
import { AxiosError } from 'axios'
import { ZONES } from './Zones'
dayjs.extend(relativeTime)
dayjs.extend(durations)

export const sleep = async (ms: number) => {
  return new Promise((resolve) => setTimeout(resolve, ms))
}

export const getAvatar = (user: UserSelfDto | UserDto | undefined): string => {
  if (!user) return 'https://cdn.discordapp.com/embed/avatars/0.png'

  const discordCdn = 'https://cdn.discordapp.com/'
  let avatar = ''
  if (!user.avatar) {
    avatar = `${discordCdn}/embed/avatars/${parseInt(user.discriminator) % 5}.png`
  } else {
    const isGIF = user.avatar.startsWith('a_')
    avatar = `${discordCdn}avatars/${user.discordId}/${user.avatar}${isGIF ? '.gif' : '.png'}`
  }

  return avatar
}

/**
 * Abbreviates a number to a string with a letter suffix. (e.g. 1000 => 1k)
 * Source: https://stackoverflow.com/a/2686098
 */
export const abbrNum = (number: number, decPlaces: number) => {
  // 2 decimal places => 100, 3 => 1000, etc
  decPlaces = Math.pow(10, decPlaces)

  let abbreviated = '0'
  // Enumerate number abbreviations
  const abbrev = ['k', 'm', 'b', 't']

  // Go through the array backwards, so we do the largest first
  for (let i = abbrev.length - 1; i >= 0; i--) {
    // Convert array index to "1000", "1000000", etc
    const size = Math.pow(10, (i + 1) * 3)

    // If the number is bigger or equal do the abbreviation
    if (size <= number) {
      // Here, we multiply by decPlaces, round, and then divide by decPlaces.
      // This gives us nice rounding to a particular decimal place.
      number = Math.round((number * decPlaces) / size) / decPlaces

      // Handle special case where we round up to the next abbreviation
      if (number == 1000 && i < abbrev.length - 1) {
        number = 1
        i++
      }

      // Add the letter for the abbreviation
      abbreviated = number + abbrev[i]

      // We are done... stop
      break
    }
  }

  return abbreviated
}

export const getParties = (encounter: EncounterSummaryDto) => {
  if (encounter.participants.length < 5) {
    return [{
      id: '1',
      players: encounter.participants,
    }];
  }

  const parties = new Map<string, EncounterParty>();
  encounter.participants.forEach((participant) => {
    if (!participant.partyId) return
    const party = parties.get(participant.partyId);
    if (party) {
      party.players.push(participant);
    } else {
      parties.set(participant.partyId, {
        id: participant.partyId,
        players: [participant],
      });
    }
  });

  return Array.from(parties.values()).map((party, i) => {
    return {
      ...party,
      id: `${i + 1}`,
    }
  });
}

/**
 * Returns a string representing the time since the given date as a relative time string.
 * (e.g. 1 hour ago)
 */
export const timeSince = (date: number) => {
  const since = dayjs(date).fromNow(false)
  return since.replace('minutes', 'min')
}

export const getDuration = (duration: number) => {
  return dayjs(duration).format('mm:ss')
}

export const getClassColor = (classId: string | number) => {
  return classColors[`${classId}`] || '#000000'
}

export const getTotalDpsInSummary = (encounter: EncounterSummaryDto) => {
  return encounter.participants.reduce((acc, participant) => {
    return acc + participant.dps;
  }, 0);
}

export const getBestPlayer = (encounter: EncounterSummaryDto) => {
  return [...encounter.participants].sort((a, b) => b.damage - a.damage)[0];
}

export const getApiException = (error: Error): ApiException | Error => {
  if (error instanceof AxiosError) {
    return error.response ? ApiException.fromAxios(error) : error;
  }
  return error;
}

export const getZone = (encounter: EncounterSummaryDto) => {
  const zoneId = encounter.association.zone;
  return ZONES.get(zoneId);
}

export const isObjectId = (id: string) => /^[a-f\d]{24}$/i.test(id);