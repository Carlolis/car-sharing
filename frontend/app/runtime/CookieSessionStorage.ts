import { HttpServerRequest } from '@effect/platform'

// import { json, TypedResponse } from 'react-router';
import { Context, Effect as T, pipe, Schema as Sc } from 'effect'
import { stringify } from 'effect/FastCheck'
import * as O from 'effect/Option'
import { commitSession, getSession } from '~/session'
import { NotAuthenticated } from './NotAuthenticatedError'
import { ServerResponse } from './ServerResponse'
// import { JwtUserInfo } from '~/routes/callback'
// import { commitSession, getSession } from '~/session'
// import { NotAuthenticated } from '../models/NotAuthenticatedError'
// import { Redirect, ServerResponse } from '../ServerResponse'

export class CookieSessionStorage
  extends T.Service<CookieSessionStorage>()('CookieSessionStorage', {
    effect: T.gen(function* (_) {
      const optionalCookies: O.Option<string> = yield* _(
        HttpServerRequest.schemaHeaders(Sc.Struct({ cookie: Sc.String })),
        T.mapError(e => NotAuthenticated.of(e.message)),
        T.map(headers => O.some(headers.cookie)),
        T.tapError(e => T.logError(`CookieSessionStorage - get cookies for service`, e.message)),
        T.catchAll(() => T.succeed(O.none()))
      )

      const commitUserInfo = (userInfo: string) =>
        T.gen(function* (_) {
          const session = yield* _(T.promise(() =>
            pipe(
              optionalCookies,
              O.getOrUndefined,
              cookies =>
                getSession(
                  cookies
                )
            )
          ))
          yield* T.logInfo(`CookieSessionStorage - commitUserInfo`, userInfo, session)

          session.set('user_info', userInfo)

          const cookie = yield* _(T.promise(() => commitSession(session)))

          return Response.json({
            body: userInfo,
            headers: {
              // eslint-disable-next-line @typescript-eslint/naming-convention
              'Set-Cookie': cookie
            }
          })
        })
      const commitUserName = (userName: string) =>
        T.gen(function* (_) {
          const session = yield* _(T.promise(() =>
            pipe(
              optionalCookies,
              O.getOrUndefined,
              cookies =>
                getSession(
                  cookies
                )
            )
          ))
          yield* T.logInfo(`CookieSessionStorage - commitUserName`, userName, session)

          session.set('userName', userName)

          const cookie = yield* _(T.promise(() => commitSession(session)))
          yield* T.logInfo(`Cookie.... ${stringify(cookie)}`)
          return cookie
          // return yield* _(ServerResponse.Redirect({
          //   location: '/login',
          //   headers: {
          //     'Set-Cookie': cookie
          //   }
          // }))
        })

      const getUserInfo = () =>
        T.gen(function* (_) {
          const cookies = yield* _(
            optionalCookies,
            T.catchAll(() => {
              return ServerResponse.Redirect({
                location: '/notauthorize'
              })
            })
          )

          const session = yield* _(T.promise(() =>
            getSession(
              cookies
            )
          ))

          const userInfo = yield* _(
            session.get('user_info'),
            Sc.decodeUnknown(Sc.String),
            T.mapError(e => NotAuthenticated.of(e.message)),
            T.tapError(e => T.logError(`CookieSessionStorage - getUserInfo`, e)),
            T.catchAll(() =>
              ServerResponse.Redirect({
                location: '/notauthorize'
              })
            )
          )

          return userInfo
        })
      const getUserName = () =>
        T.gen(function* (_) {
          const cookies = yield* _(
            optionalCookies,
            T.catchAll(() => {
              return T.succeed(undefined)
            })
          )

          const session = yield* _(T.promise(() =>
            getSession(
              cookies
            )
          ))

          return yield* _(
            session.get('userName'),
            Sc.decodeUnknown(Sc.String),
            T.catchAll(() => T.succeed(undefined))
          )
        })
      return {
        getUserInfo,
        getUserName,
        commitUserName,
        commitUserInfo
      }
    })
  }) {}

export class CookieSession
  extends Context.Tag('CookieSessionStorage')<CookieSessionStorage, CookieSessionStorage>() {
}

export const CookieSessionStorageLayer = CookieSessionStorage.Default
