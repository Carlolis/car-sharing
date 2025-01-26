import { HttpServerRequest } from '@effect/platform'
import { Match, Schema as Sc } from 'effect'
import * as T from 'effect/Effect'
import { stringify } from 'effect/FastCheck'
import { useEffect, useState } from 'react'
import { Form, useActionData, useSubmit } from 'react-router'
import { Remix } from '~/runtime/Remix'
import { Redirect } from '~/runtime/ServerResponse'
import { Api } from '~/services/api'
import { TripCreate } from '~/types/api'

export const action = Remix.unwrapAction(
  T.gen(function* () {
    yield* T.logInfo(`Creating Trip....`)
    const api = yield* Api
    return T.gen(function* () {
      const tripCreate2 = yield* HttpServerRequest.schemaBodyJson(
        Sc.Any
      )

      yield* T.logInfo(`WTF ?.... ${stringify(tripCreate2)}`)
      const tripCreate = yield* HttpServerRequest.schemaBodyForm(
        TripCreate
      )
      const token = yield* HttpServerRequest.schemaHeaders(Sc.Any)
      yield* T.logInfo(`Token ?.... ${stringify(token)}`)

      yield* T.logInfo(`Creating Trip.... ${stringify(tripCreate2)}`)
      const tripId = yield* api.createTrip(tripCreate)
      yield* T.logInfo(`Trip created .... ${stringify(tripId)}`)
      return { tripId }
    }).pipe(T.catchAll(() => new Redirect({ location: '/login' })))
  })
)

export default function CreateTrip() {
  const actionData = useActionData<typeof action>()
  const [errorMessage, setErrorMessage] = useState<string | undefined>(undefined)
  const [userName, setUserName] = useState<string | undefined>(undefined)
  const submit = useSubmit()
  useEffect(() => {
    const match = Match.type<typeof actionData>().pipe(
      Match.when(undefined, () => setErrorMessage('Bienvenue')),
      Match.orElse(({ tripId }) => {
        setUserName(tripId.tripId)
      })
    )
    match(actionData)
  }, [actionData])

  return (
    <div className="max-w-md mx-auto mt-10 px-4">
      <h2 className="text-2xl font-bold mb-6">Créer un nouveau trajet</h2>

      {errorMessage && (
        <div className="mb-4 p-4 text-green-700 bg-green-100 rounded">
          {errorMessage}
        </div>
      )}
      {userName && (
        <div className="mb-4 p-4 text-green-700 bg-green-100 rounded">
          {userName}
        </div>
      )}

      <Form
        method="post"
        className="space-y-6"
        onSubmit={e => {
          {
            console.log('submit', { ...e.currentTarget, token: localStorage.getItem('token') })

            submit(
              { ...e.currentTarget }
            )
          }
        }}
      >
        <div>
          <label className="block text-sm font-medium text-gray-700">
            Quoi ?

            <input
              type="text"
              name="name"
              required
              className="mt-1 block w-full px-3 py-2 rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
            />
          </label>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">
            Quand

            <input
              type="date"
              name="date"
              required
              defaultValue={new Date().toISOString().split('T')[0]}
              className="mt-1 block w-full px-3 py-2 rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
            />
          </label>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">
            Distance (km)

            <input
              type="number"
              name="distance"
              required
              min="0"
              step="0.1"
              className="mt-1 block w-full px-3 py-2 rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
            />
          </label>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">
            Qui ?
            <select
              name="drivers"
              required
              multiple
              className="block w-full px-3 py-2 rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
            >
              <option value="maé">Maé</option>
              <option value="charles">Charles</option>
              <option value="brigitte">Brigitte</option>
            </select>
          </label>
        </div>

        <button
          type="submit"
          className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
        >
          Créer le trajet
        </button>
      </Form>
    </div>
  )
}
