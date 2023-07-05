import { Endpoint, HttpMethod } from '../Endpoints'
import * as Responses from '../ApiResponse'
import type * as Requests from '@/api/common/dto/RequestDtos'

const ROOT_PATH = '/encounters'
const RECENT_PATH = ROOT_PATH + '/recent'

//#region Encounter View Endpoints

export class EncounterEntryEndpoint extends Endpoint<Responses.EncounterEntryResponse> {
  constructor(dto: Requests.EncounterViewDto) {
    super(Responses.EncounterEntryResponse, ROOT_PATH + '/entry', HttpMethod.POST, true, dto)
  }
}

export class EncounterSummaryEndpoint extends Endpoint<Responses.EncounterSummaryResponse> {
  constructor(dto: Requests.EncounterViewDto) {
    super(Responses.EncounterSummaryResponse, ROOT_PATH + '/summary', HttpMethod.POST, true, dto)
  }
}
//#endregion

//#region Misc. Encounter Endpoints
export class EncounterCountEndpoint extends Endpoint<Responses.EncounterCountResponse> {
  constructor(dto: Requests.EncounterViewDto) {
    super(Responses.EncounterCountResponse, ROOT_PATH + `/count`, HttpMethod.POST, true, dto)
  }
}
//#endregion

//#region Recent Encounters Endpoints

export class TopAndRecentEncountersEndpoint extends Endpoint<Responses.TopAndRecentEncountersResponse> {
  constructor(dto: Requests.EncounterViewRecentDto) {
    super(Responses.TopAndRecentEncountersResponse, RECENT_PATH, HttpMethod.POST, true, dto)
    this.headers = { 'Content-Type': 'application/json' }
  }
}

export class TopDpsEncounterEndpoint extends Endpoint<Responses.EncounterSummaryResponse> {
  constructor(dto: Requests.EncounterViewRecentDto) {
    super(Responses.EncounterSummaryResponse, RECENT_PATH + '/top', HttpMethod.POST, true, dto)
    this.headers = { 'Content-Type': 'application/json' }
  }
}

export class LatestEncountersEndpoint extends Endpoint<Responses.RecentEncountersResponse> {
  constructor(dto: Requests.EncounterViewRecentDto) {
    super(Responses.RecentEncountersResponse, RECENT_PATH + '/latest', HttpMethod.POST, true, dto)
    this.headers = { 'Content-Type': 'application/json' }
  }
}
//#endregion
