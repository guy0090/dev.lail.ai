//#region Summaries

export interface EncounterAssociation {
  players: string
  zone: number
}

export interface EncounterParticipant {
  id: string
  name?: string
  classId: number
  gearScore: number
  damage: number
  dps: number
  partyId?: string
  userId?: string
}

export interface EncounterParty {
  id: string
  players: EncounterParticipant[]
}
//#endregion

//#region Encounters
export interface EncounterEntity {
  lastUpdated: number
  id: string
  npcId: number
  classId: number
  isBoss: boolean
  isPlayer: boolean
  isEsther?: boolean
  isDead: boolean
  deaths: number
  deathTime: number
  gearScore: number
  currentHp: number
  maxHp: number
  damageDealt: number
  damageDealtDebuffedBySupport: number
  damageDealtBuffedBySupport: number
  healingDone: number
  shieldDone: number
  damageTaken: number
  shieldReceived: number
  damagePreventedWithShieldOnOthers: number
  damagePreventedByShield: number
  skills: EntitySkill[]
  hits: Hits
  icon: string
  damageDealtDebuffedBy: { [id: number | string]: number };
  damageDealtBuffedBy: { [id: number | string]: number };
  damagePreventedWithShieldOnOthersBy: { [id: number | string]: number };
  damagePreventedByShieldBy: { [id: number | string]: number };
  shieldDoneBy: { [id: number | string]: number };
  shieldReceivedBy: { [id: number | string]: number };
}

export interface EntitySkill {
  id: number
  icon: string
  damageDealt: number
  damageDealtDebuffedBySupport: number
  damageDealtBuffedBySupport: number
  maxDamage: number
  hits: Hits
  breakdown: SkillBreakdown[]
  damageDealtDebuffedBy: { [key: number | string]: number };
  damageDealtBuffedBy: { [key: number | string]: number };
}

export interface Hits {
  casts: number
  total: number
  crit: number
  backAttack: number
  totalBackAttack: number
  frontAttack: number
  totalFrontAttack: number
  counter: number
  hitsDebuffedBySupport: number
  hitsBuffedBySupport: number
  hitsBuffedBy: { [key: number | string]: number }
  hitsDebuffedBy: { [key: number | string]: number }
}

export interface SkillBreakdown {
  timestamp: number
  damage: number
  targetEntity: string
  isCrit: boolean
  isBackAttack: boolean
  isFrontAttack: boolean
  isBuffedBySupport: boolean
  isDebuffedBySupport: boolean
  debuffedBy: number[]
  buffedBy: number[]
}

export interface DamageStatistics {
  totalDamageDealt: number;
  topDamageDealt: number;
  totalDamageTaken: number;
  topDamageTaken: number;
  totalHealingDone: number;
  topHealingDone: number;
  totalShieldDone: number;
  topShieldDone: number;
  debuffs: StatusEffect[];
  buffs: StatusEffect[];
  topShieldGotten: number;
  totalEffectiveShieldingDone: number;
  topEffectiveShieldingDone: number;
  topEffectiveShieldingUsed: number;
  effectiveShieldingBuffs: StatusEffect[];
  appliedShieldingBuffs: StatusEffect[];
}

export interface StatusEffect {
  id: number;
  target: StatusEffectTarget;
  category: 'buff' | 'debuff';
  buffcategory: string;
  bufftype: StatusEffectBuffTypeFlags;
  uniquegroup: number;
  source: StatusEffectSource;
}

export enum StatusEffectTarget {
  OTHER = 0,
  PARTY = 1,
  SELF = 2,
}

export enum StatusEffectBuffTypeFlags {
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

export interface StatusEffectSource {
  icon: string;
  setname?: string;
  skill?: EffectSkill;
}

export interface EffectSkill {
  id: number;
  classid: number;
  icon: string;
  summonids?: number[];
  summonsourceskill?: number;
  sourceskill?: number;
}
//#endregion
