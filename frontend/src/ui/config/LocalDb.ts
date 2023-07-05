import localForage from 'localforage';

export const logDb = localForage.createInstance({
  name: "log_data",
  description: "Stores application logs",
})

export const encounterDb = localForage.createInstance({
  name: "encounter_data",
  description: "Stores encounter data",
  storeName: "encounter_cache",
  version: 1.0,
})
