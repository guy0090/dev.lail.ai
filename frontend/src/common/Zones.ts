/**
 * Temporary solution for zone data
 */

export enum EncounterType {
  GUARDIAN = 1,
  CHALLENGE_GUARDIAN = 2,
  LEGION_RAID = 6,
}

export class Zone {
  id: number
  gate?: number
  type: number
  icon: number

  constructor(id: number, type: number, gate?: number, icon?: number) {
    this.id = id
    this.type = type
    this.gate = gate
    this.icon = icon ?? id
  }
}

export const ZONES: Map<number, Zone> = new Map([
  [9,  new Zone(9, EncounterType.LEGION_RAID, 1, 9)],
  [10,  new Zone(10, EncounterType.LEGION_RAID, 2, 9)],
  [11,  new Zone(11, EncounterType.LEGION_RAID, 1, 11)],
  [12,  new Zone(12, EncounterType.LEGION_RAID, 2, 11)],
  [13,  new Zone(13, EncounterType.LEGION_RAID, 3, 11)],
  [14,  new Zone(14, EncounterType.LEGION_RAID, 1, 14)],
  [15,  new Zone(15, EncounterType.LEGION_RAID, 2, 14)],
  [16,  new Zone(16, EncounterType.LEGION_RAID, 3, 14)],
  [17,  new Zone(17, EncounterType.LEGION_RAID, 0, 17)],
  [18,  new Zone(18, EncounterType.LEGION_RAID, 1, 17)],
  [19,  new Zone(19, EncounterType.LEGION_RAID, 2, 17)],
  [20,  new Zone(20, EncounterType.LEGION_RAID, 3, 17)],
  [21,  new Zone(21, EncounterType.LEGION_RAID, 4, 17)],
  [22,  new Zone(22, EncounterType.LEGION_RAID, 5, 17)],
  [23,  new Zone(23, EncounterType.LEGION_RAID, 6, 17)],
  [36, new Zone(36, EncounterType.GUARDIAN)],
  [40, new Zone(40, EncounterType.GUARDIAN)],
  [41, new Zone(41, EncounterType.GUARDIAN)],
  [42, new Zone(42, EncounterType.GUARDIAN)],
  [43, new Zone(43, EncounterType.GUARDIAN)],
])