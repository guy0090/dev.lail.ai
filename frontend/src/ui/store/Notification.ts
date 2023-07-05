import type { Notification } from '@/api/common/Notification'
import { defineStore } from 'pinia'
import { socketStore } from './WebSocket'
import {
  ClearNotificationCommand,
  SetNotificationSeenCommand
} from '@/api/websocket/commands/Commands'

export const notificationStore = defineStore('notificationStore', {
  state: () => {
    return {
      notifications: [] as Notification[]
    }
  },


  getters: {
    getNotifications(state) {
      return state.notifications
    }
  },


  actions: {
    setNotifications(notifications: Notification[]) {
      this.notifications = notifications
    },


    addNotification(notification: Notification) {
      this.notifications.push(notification)
    },


    removeNotification(notification: Notification) {
      this.notifications = this.notifications.filter((n) => n.id !== notification.id)
    },


    getNotification(id: string): Notification | undefined {
      return this.notifications.find((n) => n.id === id)
    },


    async setNotificationSeen(notificationId: string) {
      const notification = this.getNotification(notificationId)
      if (!notification) throw Error('Notification not found')

      notification.seen = true

      const socket = socketStore()
      if (!socket.isConnected) throw Error('Socket not connected')

      const command = new SetNotificationSeenCommand(notification.id)
      const response = await socket.sendCommand(command)

      if (!response) throw Error('No response from server')
      if (response.error) throw Error(response.error.message)
    },


    async clearNotification(notificationId: string) {
      const notification = this.getNotification(notificationId)
      if (!notification) throw Error('Notification not found')

      const socket = socketStore()
      if (!socket.isConnected) throw Error('Socket not connected')

      const command = new ClearNotificationCommand(notification)
      const response = await socket.sendCommand(command)

      if (!response) throw Error('No response from server')
      if (response.error) throw Error(response.error.message)

      console.log('Clearing notification', notification)
      this.removeNotification(notification)
    }
  }
})
