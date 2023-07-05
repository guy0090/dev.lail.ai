/**
 * Temporarily hard coded resources until meter has dedicated database files
 */
export enum ZoneType {
  UNKNOWN = 0,
  GUARDIAN = 1,
  CHALLENGE_GUARDIAN = 2,
  ABYSS_RAID = 3,
  ABYSSAL_DUNGEON = 4,
  CHALLENGE_ABYSSAL_DUNGEON = 5,
  LEGION_RAID = 6,
}

export enum ZoneDifficulty {
  UNKNOWN = 0,
  NORMAL = 1,
  HARD = 2,
  HELL = 3,
  // Challenge? Hell achates?
}

export interface ZoneBoss {
  id: number;
  maxHp?: number;
}

export interface Zone {
  enabled: boolean;
  id: number;
  name: string;
  type: ZoneType;
  bosses: ZoneBoss[];
  difficulty?: ZoneDifficulty;
  minPlayers: 1 | 4 | 8;
  maxPlayers: 4 | 8;
}

export const zones: Zone[] = [
  {
    enabled: false,
    id: 1,
    type: ZoneType.ABYSS_RAID,
    name: 'Argos P1',
    bosses: [
      { id: 634000 }, // P1?
    ],
    minPlayers: 8,
    maxPlayers: 8,
  },
  {
    enabled: false,
    id: 2,
    type: ZoneType.ABYSS_RAID,
    name: 'Argos P2',
    bosses: [
      { id: 634010 }, // P2?
    ],
    minPlayers: 8,
    maxPlayers: 8,
  },
  {
    enabled: false,
    id: 3,
    type: ZoneType.ABYSS_RAID,
    name: 'Argos P3',
    bosses: [
      { id: 634020 }, // P3?
    ],
    minPlayers: 8,
    maxPlayers: 8,
  },
  {
    enabled: true,
    id: 9,
    type: ZoneType.LEGION_RAID,
    name: 'Valtan G1',
    bosses: [
      { id: 480005 }, // Leader Lugaru
      { id: 480006 }, // Destroyer Lucas
      { id: 480010 }, // Dark Mountain Predator
    ],
    minPlayers: 8,
    maxPlayers: 8,
  },
  {
    enabled: true,
    id: 10,
    type: ZoneType.LEGION_RAID,
    name: 'Valtan G2',
    bosses: [
      { id: 42063791 }, // Ravaged Tyrant of Beasts
      { id: 42063042 }, // Demon Beast Commander Valtan
      { id: 42060070 }, // 찢겨진 마수의 군주
    ],
    minPlayers: 8,
    maxPlayers: 8,
  },
  {
    enabled: true,
    id: 11,
    type: ZoneType.LEGION_RAID,
    name: 'Vykas G1',
    bosses: [
      { id: 480208 }, // Incubus Morphe
      { id: 480209 }, // Nightmarish Morphe
    ],
    minPlayers: 8,
    maxPlayers: 8,
  },
  {
    enabled: true,
    id: 12,
    type: ZoneType.LEGION_RAID,
    name: 'Vykas G2',
    bosses: [
      { id: 480210 }, // Covetous Devourer Vykas
    ],
    minPlayers: 8,
    maxPlayers: 8,
  },
  {
    enabled: true,
    id: 13,
    type: ZoneType.LEGION_RAID,
    name: 'Vykas G3',
    bosses: [
      { id: 480218 }, // Covetous Legion Commander Vykas
    ],
    minPlayers: 8,
    maxPlayers: 8,
  },
  {
    enabled: true,
    id: 14,
    type: ZoneType.LEGION_RAID,
    name: 'Kakul-Saydon G1',
    bosses: [
      { id: 480601 }, // Saydon
      { id: 480604 }, // Saydon
    ],
    minPlayers: 4,
    maxPlayers: 4,
  },
  {
    enabled: true,
    id: 15,
    type: ZoneType.LEGION_RAID,
    name: 'Kakul-Saydon G2',
    bosses: [
      { id: 480611 }, // Kakul
    ],
    minPlayers: 4,
    maxPlayers: 4,
  },
  {
    enabled: true,
    id: 16,
    type: ZoneType.LEGION_RAID,
    name: 'Kakul-Saydon G3',
    bosses: [
      { id: 480631 }, // Kakul-Saydon
      { id: 480635 }, // Encore-Desiring Kakul-Saydon
    ],
    minPlayers: 4,
    maxPlayers: 4,
  },
  {
    enabled: true,
    id: 17,
    type: ZoneType.LEGION_RAID,
    name: 'Brelshaza G0',
    bosses: [
      { id: 480815 }, // Brelshaza, Monarch of Nightmares
    ],
    minPlayers: 8,
    maxPlayers: 8,
  },
  {
    enabled: true,
    id: 18,
    type: ZoneType.LEGION_RAID,
    name: 'Brelshaza G1',
    bosses: [
      { id: 480802 }, // Gehenna Helkasirs
      { id: 480803 }, // Nightmare Gehenna
      { id: 480804 }, // Nightmare Helkasirs
      { id: 480805 }, // Crushing Phantom Wardog
      { id: 480806 }, // Grieving Statue
      { id: 480807 }, // Furious Statue
      { id: 480874 }, // Molting Phantom Wardog
      { id: 480875 }, // Echoing Phantom Wardog
      { id: 480876 }, // Raging Phantom Wardog
      { id: 480877 }, // Despairing Statue
      { id: 480878 }, // Eroded Statue
    ],
    minPlayers: 8,
    maxPlayers: 8,
  },
  {
    enabled: true,
    id: 19,
    type: ZoneType.LEGION_RAID,
    name: 'Brelshaza G2',
    bosses: [
      { id: 480808 }, // Prokel
      { id: 480809 }, // Prokel's Spiritual Echo
    ],
    minPlayers: 8,
    maxPlayers: 8,
  },
  {
    enabled: true,
    id: 20,
    type: ZoneType.LEGION_RAID,
    name: 'Brelshaza G3',
    bosses: [
      { id: 480810 }, // Ashtarot
    ],
    minPlayers: 8,
    maxPlayers: 8,
  },
  {
    enabled: true,
    id: 21,
    type: ZoneType.LEGION_RAID,
    name: 'Brelshaza G4',
    bosses: [
      { id: 480811 }, // Primal Nightmare
    ],
    minPlayers: 8,
    maxPlayers: 8,
  },
  {
    enabled: true,
    id: 22,
    type: ZoneType.LEGION_RAID,
    name: 'Brelshaza G5',
    bosses: [
      { id: 480813 }, // Brelshaza, Monarch of Nightmares
    ],
    minPlayers: 8,
    maxPlayers: 8,
  },
  {
    enabled: true,
    id: 23,
    type: ZoneType.LEGION_RAID,
    name: 'Brelshaza G6',
    bosses: [
      { id: 480814 }, // Phantom Legion Commander Brelshaza
    ],
    minPlayers: 8,
    maxPlayers: 8,
  },
  {
    enabled: true, // Used for testing
    id: 36,
    type: ZoneType.GUARDIAN,
    name: 'Chromanium',
    bosses: [{ id: 620030 }],
    minPlayers: 1,
    maxPlayers: 4,
  },
  {
    enabled: true,
    id: 40,
    type: ZoneType.GUARDIAN,
    name: 'Deskaluda',
    bosses: [{ id: 620260 }],
    minPlayers: 1,
    maxPlayers: 4,
  },
  {
    enabled: true,
    id: 41,
    type: ZoneType.GUARDIAN,
    name: 'Kungelanium',
    bosses: [{ id: 620290 }],
    minPlayers: 1,
    maxPlayers: 4,
  },
  {
    enabled: true,
    id: 42,
    type: ZoneType.GUARDIAN,
    name: 'Caliligos',
    bosses: [{ id: 620250 }],
    minPlayers: 1,
    maxPlayers: 4,
  },
  {
    enabled: true,
    id: 43,
    type: ZoneType.GUARDIAN,
    name: 'Hanumatan',
    bosses: [{ id: 620280 }],
    minPlayers: 1,
    maxPlayers: 4,
  },
];

export const getZone = (id: number): Zone | undefined => zones.find(zone => zone.id === id);

export const determineZone = (npcId: number): Zone | undefined => {
  return zones.find(zone => {
    return zone.bosses.some(boss => {
      return boss.id === npcId;
    });
  });
};
