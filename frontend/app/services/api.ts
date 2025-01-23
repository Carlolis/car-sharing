import { HttpBody, HttpClient, HttpClientRequest } from '@effect/platform'
import { Context, pipe } from 'effect'
import * as T from 'effect/Effect'
import { stringify } from 'effect/FastCheck'
import * as L from 'effect/Layer'
import { Trip, TripCreate, TripStats } from '../types/api'
const API_URL = 'http://localhost:8080/api'

export class ApiError extends Error {
  constructor(public statusCode: number, message: string) {
    super(message)
  }
}

async function handleResponse<T,>(response: Response): Promise<T> {
  if (!response.ok) {
    const error = await response.clone().json()
    throw new ApiError(response.status, error.message)
  }
  return response.clone().json()
}

export const makeApiHttp = () => {
  const login = (login: string) => {
    return T.gen(function* () {
      const defaultClient = yield* HttpClient.HttpClient
      const loginUrl = HttpClientRequest.get(`${API_URL}/login`)

      const body = yield* HttpBody.json({ name: login })
      const titi = pipe(
        loginUrl,
        HttpClientRequest.setHeader('Content-Type', 'application/json'),
        HttpClientRequest.setBody(body),
        HttpClientRequest.setMethod('POST')
      )

      const response = yield* defaultClient.execute(titi)
      yield* T.logInfo('login http response :', response)
      const responseJson = yield* response.json

      if (response.status === 401) {
        yield* T.logInfo(response.status === 401)
        yield* T.fail(stringify(responseJson))
      }

      return responseJson as { token: string }
    }).pipe(
      T.scoped
    )
  }

  return ({
    login
  })
}

export class Api extends Context.Tag('Api')<
  Api,
  ReturnType<typeof makeApiHttp>
>() {
}

export const ApiLayer = L.succeed(Api, makeApiHttp())

export const api = {
  login(login: string) {
    return T.gen(function* () {
      const defaultClient = yield* HttpClient.HttpClient
      const toto = HttpClientRequest.get(`${API_URL}/login`)

      const body = yield* HttpBody.json({ name: login })
      const titi = pipe(
        toto,
        HttpClientRequest.setHeader('Content-Type', 'application/json'),
        HttpClientRequest.setBody(body),
        HttpClientRequest.setMethod('POST')
      )

      const response = yield* defaultClient.execute(titi)
      yield* T.logInfo(response)
      const responseJson = yield* response.json

      if (response.status === 401) {
        yield* T.logInfo(response.status === 401)
        yield* T.fail(stringify(responseJson))
      }

      return responseJson as { token: string }
    }).pipe(
      T.scoped
    )
  },

  async createTrip(trip: TripCreate, token: string): Promise<Trip> {
    const response = await fetch(`${API_URL}/trips`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(trip)
    })
    return handleResponse<Trip>(response)
  },

  async getUserStats(token: string): Promise<TripStats> {
    const response = await fetch(`${API_URL}/trips/user`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    return handleResponse<TripStats>(response)
  },

  getTotalStats() {
    return T.gen(function* () {
      const defaultClient = yield* HttpClient.HttpClient
      const toto = HttpClientRequest.get(`${API_URL}/trips/total`)

      const response = yield* defaultClient.execute(toto)
      const responseJson = yield* response.json
      return responseJson as TripStats
    }).pipe(
      T.scoped
    )
  }
}
