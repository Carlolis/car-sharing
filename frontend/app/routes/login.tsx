import { HttpServerRequest } from '@effect/platform'

import { Match, Schema as Sc } from 'effect'
import * as T from 'effect/Effect'
import { stringify } from 'effect/FastCheck'
import { useEffect, useState } from 'react'
import { Form, useActionData, useNavigate } from 'react-router'
import { Remix } from '~/runtime/Remix'

import { CookieSessionStorage } from '~/runtime/CookieSessionStorage'
import { useAuth } from '../contexts/AuthContext'
import { Api } from '../services/api'

const UserNotFound = Sc.TaggedStruct('NotFound', {
  message: Sc.String
})

export const action = Remix.action(
  T.gen(function* () {
    const cookieSession = yield* CookieSessionStorage
    const api = yield* Api
    const { username } = yield* HttpServerRequest.schemaBodyForm(
      Sc.Struct({
        username: Sc.String
      })
    )

    yield* T.logInfo(`Login.... ${username}`)
    const { token } = yield* api.login(username)
    yield* T.logInfo(`Token.... ${stringify(token)}`)

    cookieSession.getUserInfo()

    return { token, username }
  }).pipe(T.catchAll(error =>
    // Only for 404 if you want to do something with it

    Sc.decode(UserNotFound)({ message: error.toString(), _tag: 'NotFound' })
  ))
)

export default function Login() {
  const { setAuth } = useAuth()
  const [isNotFound, setIsNotFound] = useState(false)
  const [errorMessage, setErrorMessage] = useState('')
  const navigate = useNavigate()
  const actionData = useActionData<typeof action>()

  useEffect(() => {
    const match = Match.type<typeof actionData>().pipe(
      Match.when(undefined, () => setIsNotFound(false)),
      Match.tag('NotFound', ({ message }) => {
        setIsNotFound(true)
        setErrorMessage(message)
      }),
      Match.when({ token: Match.string }, ({ token, username }) => {
        setAuth({ token, username })
        navigate('/dashboard')
      }),
      Match.exhaustive
    )
    match(actionData)
  }, [actionData])

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 dark:bg-gray-900 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900 dark:text-white">
            Connexion
          </h2>
        </div>
        <Form method="post" className="mt-8 space-y-6">
          {isNotFound && (
            <div className="rounded-md bg-red-50 dark:bg-red-900 p-4">
              <div className="text-sm text-red-700 dark:text-red-200">
                Utilisateur non trouvé {errorMessage}
              </div>
            </div>
          )}
          <div className="rounded-md shadow-sm -space-y-px">
            <div>
              <input
                type="username"
                name="username"
                required
                className="appearance-none rounded-md relative block w-full px-3 py-2 border border-gray-300 dark:border-gray-600 placeholder-gray-500 dark:placeholder-gray-400 text-gray-900 dark:text-white bg-white dark:bg-gray-800 rounded-t-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                placeholder="Login"
              />
            </div>
          </div>

          <div>
            <button
              type="submit"
              className="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
            >
              Se connecter
            </button>
          </div>
        </Form>
      </div>
    </div>
  )
}
