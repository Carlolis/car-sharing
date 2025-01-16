import { FetchHttpClient, Path } from '@effect/platform'
import { NodeFileSystem } from '@effect/platform-node'
import { Layer, Logger, LogLevel, pipe } from 'effect'
import * as T from 'effect/Effect'
import * as L from 'effect/Layer'

export const AppLayer = pipe(
  NodeFileSystem.layer,
  L.provideMerge(Path.layer),
  L.provideMerge(FetchHttpClient.layer),
  Layer.provide(Logger.minimumLogLevel(LogLevel.All))
)
