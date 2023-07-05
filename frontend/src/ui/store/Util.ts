import { defineStore } from "pinia";

export const utilStore = defineStore("utilStore", {
  state: () => {
    return {
      loading: false,
      activeMainNav: 'home',
      showEncounterPlayerNames: parseInt(localStorage.getItem('sShowEncounterPlayerNames') || '0') === 1,
      encounterOrientation: parseInt(localStorage.getItem('sEncounterOrientation') || '0'),
    }
  },

  getters: {
    getLoading(state) {
      return state.loading;
    },

    getEncounterOrientation(state) {
      return state.encounterOrientation;
    }
  },

  actions: {
    setLoading(loading: boolean) {
      this.loading = loading;
    },

    toggleLoading() {
      this.loading = !this.loading;
    },

    setActiveNav(nav: string) {
      this.activeMainNav = nav;
    },

    setShowEncounterPlayerNames(show: boolean) {
      this.showEncounterPlayerNames = show;
      localStorage.setItem('sShowEncounterPlayerNames', show ? '1' : '0');
    },

    toggleShowEncounterPlayerNames() {
      this.showEncounterPlayerNames = this.showEncounterPlayerNames ? false : true;
      localStorage.setItem('sShowEncounterPlayerNames', this.showEncounterPlayerNames ? '1' : '0');
    },

    setEncounterOrientation(orientation: number) {
      this.encounterOrientation = orientation;
      localStorage.setItem('sEncounterOrientation', orientation.toString());
    },

  }

});