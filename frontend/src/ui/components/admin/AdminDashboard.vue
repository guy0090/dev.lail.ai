<template>
  <v-container fluid>
    <v-row>
      <v-col class="pb-0">
        <h2>
          <v-icon icon="mdi-view-carousel-outline" />
          Instance overview
        </h2>
        <v-divider class="mt-1" />
      </v-col>
    </v-row>
    <v-row class="px-0">
      <resource-card icon="mdi-post-outline" title="encounters" subtitle="Manage encounters" :amount="0" route="" />
      <resource-card icon="mdi-shield-account" title="users" subtitle="Manage users" :amount="0" route="" />
      <resource-card icon="mdi-card-account-details-outline" title="roles" subtitle="Manage roles" :amount="0" route="" />
    </v-row>
    <v-row class="ma-2">
      <v-btn variant="outlined" color="green" @click="getUserInfo">Send API Request</v-btn>
    </v-row>
    <v-row class="ma-2">
      <v-btn variant="outlined" color="green" @click="getFollowing">Get Following</v-btn>
    </v-row>
    <v-row class="ma-2">
      <v-btn variant="outlined" color="green" @click="getFollowerCount">Get Follower Count</v-btn>
    </v-row>
    <v-row class="ma-2">
      <v-btn variant="outlined" color="green" @click="followUser">Follow User</v-btn>
    </v-row>
    <v-row class="ma-2">
      <v-btn variant="outlined" color="green" @click="unfollowUser">Unfollow User</v-btn>
    </v-row>
    <v-row class="ma-2">
      <v-btn variant="outlined" color="green" @click="forceFollowUser">Force Follow User</v-btn>
    </v-row>
    <v-row class="ma-2">
      <v-btn variant="outlined" color="green" @click="forceUnfollowUser">Force Unfollow User</v-btn>
    </v-row>
    <v-row class="ma-2">
      <v-btn variant="outlined" color="green" @click="loadEncounter()">Load Encounter</v-btn>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import ResourceCard from './ResourceCard.vue';

import * as RestApi from '@/api/http/Api';
import { GetUserEndpoint } from '@/api/http/endpoints/Users';
import { UserViewDto } from '@/api/common/dto/RequestDtos';
import { FollowUserCommand, GetFollowersCommand, GetFollowingCommand, UnfollowUserCommand, ForceFollowUserCommand, ForceUnfollowUserCommand } from '@/api/websocket/commands/Commands';
import { socketStore } from '@/ui/store/WebSocket';
import { encounterStore } from '@/ui/store/Encounters';

const ws = socketStore()
const encounters = encounterStore()

const getUserInfo = () => {
  const user = UserViewDto.fromId("644e8030c798e239203b552a")
  const request = new GetUserEndpoint(user)
  RestApi.sendApiRequest(request).then((res) => {
    console.log(res.getResult())
  }).catch((err) => {
    console.error(err)
  })
}

const getFollowerCount = async () => {
  const command = new GetFollowersCommand("644e8030c798e239203b552a")
  const response = await ws.sendCommand(command)

  if (!response) throw new Error("No response")
  if (response.error || !response.data) throw new Error(response.error?.message || "No data")

  const { result } = command.getResult(response.data)
  console.log(result)
}

const getFollowing = async () => {
  const command = new GetFollowingCommand()
  const response = await ws.sendCommand(command)

  if (!response) throw new Error("No response")
  if (response.error || !response.data) throw new Error(response.error?.message || "No data")

  const { result } = command.getResult(response.data)
  console.log(result)
}

const followUser = async () => {
  const command = new FollowUserCommand("000000000000000000000001")
  const response = await ws.sendCommand(command)

  if (!response) throw new Error("No response")
  if (response.error || !response.data) throw new Error(response.error?.message || "No data")

  const result = command.getResult(response.data)
  console.log(result)
}

const unfollowUser = async () => {
  const command = new UnfollowUserCommand("000000000000000000000001")
  const response = await ws.sendCommand(command)

  if (!response) throw new Error("No response")
  if (response.error || !response.data) throw new Error(response.error?.message || "No data")

  const result = command.getResult(response.data)
  console.log(result)
}

const forceFollowUser = async () => {
  const command = new ForceFollowUserCommand("64554ad1c27f14199ef0cae4", "644e8030c798e239203b552a")
  const response = await ws.sendCommand(command)

  if (!response) throw new Error("No response")
  if (response.error || !response.data) throw new Error(response.error?.message || "No data")

  const result = command.getResult(response.data)
  console.log(result, response.data.message)
}

const forceUnfollowUser = async () => {
  const command = new ForceUnfollowUserCommand("64554ad1c27f14199ef0cae4", "644e8030c798e239203b552a")
  const response = await ws.sendCommand(command)

  if (!response) throw new Error("No response")
  if (response.error || !response.data) throw new Error(response.error?.message || "No data")

  const result = command.getResult(response.data)
  console.log(result, response.data.message)
}

const loadEncounter = async () => {
  console.time("loadEncounter:outer")
  const ids = [
    "646e29555dc2033e7673efab",
    "646e772f5dc2033e7673efee"
  ]

  for (const id of ids) {
    console.time(`loadEncounter:inner >> ${id}`)
    const enc = await encounters.getEncounterEntry(id)
    console.log(enc)
    console.timeEnd(`loadEncounter:inner >> ${id}`)
  }

  console.timeEnd("loadEncounter:outer")
}
</script>
