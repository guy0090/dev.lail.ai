import { AxiosError, type AxiosRequestConfig, type AxiosResponse } from 'axios'

import { API_HOST } from '@/api/http/Api'
import axios from 'axios'
import { ApiException } from './ApiException'
import { EmptyResponse, type ApiResponse } from './ApiResponse'

export enum HttpMethod {
  CONNECT = 'CONNECT',
  DELETE = 'DELETE',
  GET = 'GET',
  HEAD = 'HEAD',
  OPTIONS = 'OPTIONS',
  PATCH = 'PATCH',
  POST = 'POST',
  PUT = 'PUT',
  TRACE = 'TRACE'
}

export abstract class Endpoint<Response extends ApiResponse> {
  path: string
  body: any
  method: HttpMethod
  headers?: { [key: string]: string }
  requiresCredentials: boolean
  validateStatus: ((status: number) => boolean) | undefined

  // Used to generate the response object
  clazz: new (data: any) => Response

  constructor(
    clazz: new (data: any) => Response,
    path: string,
    method: HttpMethod = HttpMethod.GET,
    requiresCredentials: boolean = false,
    body?: any,
    headers?: { [key: string]: string }
  ) {
    this.clazz = clazz
    this.path = API_HOST + path
    this.method = method
    this.requiresCredentials = requiresCredentials
    this.body = body
    this.headers = headers
  }

  protected getOptions(): AxiosRequestConfig {
    const options: AxiosRequestConfig = {
      url: this.path,
      method: this.method
    }

    // Manually handle status codes
    if (this.validateStatus) options.validateStatus = this.validateStatus.bind(this)
    if (this.requiresCredentials) options.withCredentials = true
    if (this.body) options.data = this.body
    if (this.headers) options.headers = this.headers

    return options
  }

  /**
   * @throws {ApiException} If the response is not successful
   */
  async sendRequest(): Promise<Response> {
    try {
      const res: AxiosResponse<any> = await axios(this.getOptions())
      if (this.clazz.name === EmptyResponse.name) return new this.clazz(res)
      else return new this.clazz(res.data)
    } catch (err: any) {
      if (err instanceof AxiosError && err.response) {
        throw new ApiException(err.response.data)
      } else {
        throw new ApiException({
          status: err.status ?? 500,
          message: err.message,
          path: this.path,
          timestamp: new Date().toISOString()
        })
      }
    }
  }
}
