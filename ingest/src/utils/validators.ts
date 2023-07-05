import { Entity, Skill } from '@/dtos/encounter.dto';
import { registerDecorator, ValidationArguments, ValidationOptions } from 'class-validator';
import { logger } from './logger';

export function MaxSkills(validationOptions?: ValidationOptions) {
  const maxDefault = 55;
  const maxPlayers = 55;
  const maxBosses = 100;

  return function (object: Object, propertyName: string) {
    registerDecorator({
      name: 'MaxSkills',
      target: object.constructor,
      propertyName: propertyName,
      constraints: [],
      options: validationOptions,
      validator: {
        validate(skills: Skill[], args: ValidationArguments) {
          if (!Array.isArray(skills)) return false;
          const parent = args.object as Entity;

          if (!parent.isPlayer && !parent.isBoss) return skills.length <= maxDefault;
          if (parent.isPlayer) return skills.length <= maxPlayers;
          if (parent.isBoss) return skills.length <= maxBosses;
          return false;
        },
      },
    });
  };
}

/**
 * TODO: This is more complicated than it seems, some bosses never "die", i.e.: Valtan G1 lucas/lugaru
 * Will have to think of something, or reconsider this condition entirely
 */
export function ValidCurrentHp(validationOptions?: ValidationOptions) {
  return function (object: Object, propertyName: string) {
    registerDecorator({
      name: 'ValidCurrentHp',
      target: object.constructor,
      propertyName: propertyName,
      constraints: [],
      options: validationOptions,
      validator: {
        validate(currentHp: number, args: ValidationArguments) {
          const entity = args.object as Entity;
          if (currentHp > entity.maxHp) return false;
          return true;
        },
      },
    });
  };
}

export function ValidDeaths(validationOptions?: ValidationOptions) {
  return function (object: Object, propertyName: string) {
    registerDecorator({
      name: 'ValidDeaths',
      target: object.constructor,
      propertyName: propertyName,
      constraints: [],
      options: validationOptions,
      validator: {
        validate(deaths: number, args: ValidationArguments) {
          const entity = args.object as Entity;
          if (deaths > 0 && entity.deathTime === 0) return false;
          return true;
        },
      },
    });
  };
}

export function ValidDeathTime(validationOptions?: ValidationOptions) {
  return function (object: Object, propertyName: string) {
    registerDecorator({
      name: 'ValidDeathTime',
      target: object.constructor,
      propertyName: propertyName,
      constraints: [],
      options: validationOptions,
      validator: {
        validate(deathTime: number, args: ValidationArguments) {
          const entity = args.object as Entity;
          if (deathTime > 0 && entity.deaths === 0) return false;
          return true;
        },
      },
    });
  };
}

export function ValidGearScore(validationOptions?: ValidationOptions) {
  const maxGearScore = 2000; // TODO: remember to adapt this

  return function (object: Object, propertyName: string) {
    registerDecorator({
      name: 'ValidGearScore',
      target: object.constructor,
      propertyName: propertyName,
      constraints: [],
      options: validationOptions,
      validator: {
        validate(gearScore: number, args: ValidationArguments) {
          if (gearScore > maxGearScore) return false;
          const entity = args.object as Entity;
          if (entity.isPlayer) return gearScore >= 1; // Players must have a gear score
          return true;
        },
      },
    });
  };
}

export function ValidEntities(validationOptions?: ValidationOptions) {
  const minPlayers = 1;
  const maxPlayers = 8;
  const minBosses = 1;
  const maxBosses = 4; // FIXME: What would be this max? Highest I can think of is 3? // Theres more, sometimes hidden entities are used for mechanics
  const maxEsthers = 3;

  const minEntities = minBosses + minPlayers;
  const maxEntities = maxBosses + maxPlayers + maxEsthers;

  return function (object: Object, propertyName: string) {
    const maxDamageTolerance = 5.05; // TODO: Lower this once we validate HP properly

    registerDecorator({
      name: 'ValidEntities',
      target: object.constructor,
      propertyName: propertyName,
      constraints: [],
      options: validationOptions,
      validator: {
        validate(entities: Entity[]) {
          if (!Array.isArray(entities) || entities.length > maxEntities || entities.length < minEntities) return false;

          const aggregate = entities.reduce(
            (acc, curr) => {
              if (curr.isPlayer) {
                acc.players++;
                acc.entityDamage += curr.damageDealt;
              }
              if (curr.isBoss) {
                acc.bosses++;
                acc.maxPossibleDamageDealt += curr.maxHp; // FIXME: kind of worthless if we arent validating maxHp of each npc
              }
              if (curr.isEsther) {
                acc.esthers++;
                acc.entityDamage += curr.damageDealt;
              }

              return acc;
            },
            { players: 0, bosses: 0, esthers: 0, maxPossibleDamageDealt: 0, entityDamage: 0 },
          );
          aggregate.maxPossibleDamageDealt *= maxDamageTolerance;

          const validAggregate =
            aggregate.players >= minPlayers &&
            aggregate.players <= maxPlayers &&
            aggregate.bosses >= minBosses &&
            aggregate.bosses <= maxBosses &&
            aggregate.esthers <= maxEsthers &&
            aggregate.entityDamage <= aggregate.maxPossibleDamageDealt;

          const bosses = entities.filter(entity => entity.isBoss);

          const validEntities = entities.every(entity => {
            if (entity.isBoss) return entity.maxHp <= aggregate.maxPossibleDamageDealt;
            else return entity.damageDealt <= aggregate.maxPossibleDamageDealt;
          });

          logger.debug(`
            Bosses:: ${bosses.map(boss => boss.name + '::' + boss.npcId).join(', ')}\t
            Validating Aggregate::\t
            Valid aggregate: ${validAggregate}\t
            Min players: ${aggregate.players >= minPlayers}\t
            Max players: ${aggregate.players <= maxPlayers}\t
            Min bosses: ${aggregate.bosses >= minBosses}\t
            Max bosses: ${aggregate.bosses <= maxBosses}\t
            Max esthers: ${aggregate.esthers <= maxEsthers}\t
            Max damage: ${aggregate.entityDamage <= aggregate.maxPossibleDamageDealt}\t
            Validating Entities::\t
            Valid entities: ${validEntities}`);

          const valid = validAggregate && validEntities;
          if (!valid) {
            logger.error(`Entities Validator Failed:: Valid Aggrigate: ${validAggregate} <> Valid Entities: ${validEntities}`);
          }

          return valid;
        },
      },
    });
  };
}
