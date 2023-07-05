import { defineStore } from 'pinia'
import { userStore } from './User'
import { WebSocketEvent } from '@/api/websocket/EventTypes'
import { handleEvent } from '@/api/websocket/EventHandler'
import type { EventBody } from '@/api/websocket/messages/Messages'
import { SOCKET_HOST } from '@/api/http/Api'
import type { WebSocketCommand } from '@/api/websocket/commands/Commands'
import { WebSocketResponse, DeferredResponse, type WebSocketResponseData } from '@/api/websocket/commands/CommandResponses'

export const socketStore = defineStore('socketStore', {
  state: () => {
    return {
      server: SOCKET_HOST,
      socket: {} as WebSocket,
      connected: false,
      reconnecting: false,
      retryInterval: 10000,
      retryTimer: null as ReturnType<typeof setInterval> | null,
      pending: {} as { [key: string]: DeferredResponse }
    }
  },

  getters: {
    getServer(state) {
      return state.server
    },

    getSocket(state) {
      return state.socket
    },

    isConnected(state) {
      return state.connected
    }
  },

  actions: {
    connect() {
      if (this.connected) return
      const user = userStore()
      const token: string | undefined = user.token

      try {
        if (token) {
          // console.log('User is logged in, using token.')
          this.socket = new WebSocket(`${this.server}/events?token=${token}`)
        } else {
          // console.log('User is not logged in, using no token.')
          this.socket = new WebSocket(`${this.server}/events`)
        }
      } catch (err: any) {
        console.error('Error connecting to websocket:', err.message)
      }

      this.socket.onopen = () => {
        this.setConnected(true)
        if (this.reconnecting && this.retryTimer != null) {
          this.reconnecting = false
          clearInterval(this.retryTimer)
          this.retryTimer = null
        }
        // console.log('Websocket connected')
      }

      this.socket.onclose = (event: CloseEvent) => {
        this.setConnected(false)
        if (event.code !== 1000 && !this.reconnecting) {
          // Backend shut down or restarted
          console.log('WebSocket: Disconnected, attempting to reconnect...')
          this.reconnecting = true
          if (this.retryTimer === null) {
            this.retryTimer = setInterval(() => {
              this.reconnect()
            }, this.retryInterval)
          }
        }
      }

      this.socket.onerror = (err) => {
        console.log('Error with websocket: ', err)
      }

      this.socket.onmessage = (msg) => {
        try {
          const data = JSON.parse(msg.data)
          const event = WebSocketEvent.fromId(data.event)
          const content = handleEvent(event, data.content)

          if (content instanceof WebSocketResponse) {
            if (this.pending[content.assoc]) {
              this.pending[content.assoc].resolve(content)
              delete this.pending[content.assoc]
            }
          } else {
            this.messageReceived(content)
          }
        } catch (err: any) {
          console.error('Error parsing websocket message: ', err.message)
        }
      }
    },

    disconnect() {
      if (!this.connected) return
      this.socket.close()
    },

    reconnect() {
      this.disconnect()
      setTimeout(() => {
        this.connect()
      }, 100)
    },

    setConnected(connected: boolean) {
      this.connected = connected
    },

    sendCommand<T extends WebSocketResponseData>(
      command: WebSocketCommand<T>, timeout = 5000
    ): Promise<WebSocketResponse<T>> | undefined {
      if (!this.connected) return undefined

      this.pending[command.assoc] = new DeferredResponse<T>()
      setTimeout(() => {
        if (this.pending[command.assoc]) {
          this.pending[command.assoc].reject(new Error('Request timed out'))
          delete this.pending[command.assoc]
        }
      }, timeout)

      this.socket.send(JSON.stringify(command))
      return this.pending[command.assoc].deferral
    },

    messageReceived(body: EventBody) {
      return body
    }
  }
})
