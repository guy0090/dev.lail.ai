import type { EventBody, Welcome } from '@/api/websocket/messages/Messages';
import { defineStore } from 'pinia'

export const backendStore = defineStore('backendStore', {
  state: () => {
    return {
      version: localStorage.getItem("bVersion") || "0.0.0-SNAPSHOT",
      encounters: localStorage.getItem("bEncounters") || "0",
      backendReady: true,
    }
  },

  getters: {
    getVersion(state) {
      return state.version;
    },

    getEncounters(state) {
      return state.encounters;
    },

    isBackendReady(state) {
      return state.backendReady;
    }
  },

  actions: {

    load() {
      const version = this.getItem("bVersion")
      if (version) this.version = version;
      const encounters = this.getItem("bEncounters")
      if (encounters) this.encounters = encounters;
    },

    setItem(key: string, value: any): void {
      localStorage.setItem(key, value);
    },

    getItem(key: string): string | null {
      return localStorage.getItem(key)
    },

    handleWelcomeEvent(event: EventBody) {
      const data = event as Welcome;
      this.setVersion(data.getApiVersion());
      this.setEncounters(data.getEncounters());
      this.setBackendReady(data.isBackendReady());
    },

    setVersion(version?: string) {
      if (!version) return;
      this.version = version;
      this.setItem("bVersion", version);
    },

    setEncounters(encounters?: number) {
      if (encounters === undefined) return; // 0/1 is a valid boolean *rage*

      this.encounters = `${encounters}`;
      this.setItem("bEncounters", `${encounters}`);
    },

    setBackendReady(backendReady: boolean) {
      this.backendReady = backendReady;
    }
  }
})
