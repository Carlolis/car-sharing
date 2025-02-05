import { HttpServerRequest } from '@effect/platform'
import { pipe, Schema as Sc } from 'effect'
import * as T from 'effect/Effect'
import { Unexpected } from 'effect/ParseResult'
import { Ollama } from 'ollama'

import { useEffect, useState } from 'react'
import { CgSpinner } from 'react-icons/cg'
import { FiCommand } from 'react-icons/fi'
import { LuLoaderCircle } from 'react-icons/lu'
import { Form, useActionData } from 'react-router'
import { ChatChunk, streamResponse } from '~/contexts/ia.util'
import { Remix } from '~/runtime/Remix'

export const action = Remix.action(
  T.gen(function* () {
    const { message, model } = yield* HttpServerRequest.schemaBodyForm(
      Sc.Struct({
        message: Sc.String,
        model: Sc.String
      })
    )

    const ollama = new Ollama({
      host: process.env.OLLAMA_HOST
    })

    const chatResponse = yield* pipe(
      T.promise(() =>
        ollama.chat({
          model,
          messages: [{ content: message, role: 'user' }],
          stream: true
        })
      ),
      T.map(streamResponse)
    )

    return chatResponse
  }).pipe(
    T.scoped,
    T.tapError(T.logError),
    T.catchAll(error => T.fail(new Unexpected(error)))
  )
)

export default function IA() {
  const actionData = useActionData<typeof action>()

  const [isLoading, setIsLoading] = useState<boolean>(false)
  const [texte, setTexte] = useState('')

  console.log('First render', actionData)

  const handleChatChunk = (chat: ChatChunk) => {
    if (chat.type === 'text') {
      console.log('Set Texte first', chat.content)
      setIsLoading(false)
      setTexte(content => content + chat.content)
      chat.next?.then(nextChat => {
        handleChatChunk(nextChat)
      })
    }
  }

  useEffect(() => {
    console.log('useEffect')

    if (actionData) {
      handleChatChunk(actionData)
    }
  }, [actionData])

  console.log(texte)

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100 dark:bg-gray-900 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900 dark:text-white">
            Demandez à l'IA
          </h2>
        </div>
        <Form className="mt-8 space-y-6" method="post">
          <div className="rounded-md shadow-sm -space-y-px">
            <div className="flex space-x-2">
              <div className="flex-1">
                <label htmlFor="message" className="sr-only">
                  Message
                </label>
                <input
                  id="message"
                  name="message"
                  type="text"
                  required
                  className="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 dark:border-gray-600 placeholder-gray-500 dark:placeholder-gray-400 text-gray-900 dark:text-white bg-white dark:bg-gray-800 rounded-t-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                  placeholder="Demandez à l'IA"
                />
              </div>
              <div className="flex-1">
                <label htmlFor="model" className="sr-only">
                  Modèle
                </label>
                <select
                  id="model"
                  name="model"
                  required
                  className=" rounded-none relative block w-full px-3 py-2 border border-gray-300 dark:border-gray-600 placeholder-gray-500 dark:placeholder-gray-400 text-gray-900 dark:text-white bg-white dark:bg-gray-800 rounded-b-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm cursor-pointer"
                >
                  <option value="codestral:latest">Codestral Latest</option>
                  <option value="mistral-small:24b">Mistral Small 24B</option>
                  <option value="deepseek-coder-v2:latest">DeepSeek Coder V2 Latest</option>
                  <option value="deepseek-r1:32b-qwen-distill-q4_K_M">
                    DeepSeek R1 32B Qwen Distill Q4 K M
                  </option>
                  <option value="deepseek-r1:14b-qwen-distill-q4_K_M">
                    DeepSeek R1 14B Qwen Distill Q4 K M
                  </option>
                  <option value="deepseek-r1:latest">DeepSeek R1 Latest</option>
                </select>
              </div>
            </div>
          </div>

          <div>
            <button
              type="submit"
              onClick={() => {
                setTexte('')

                setIsLoading(true)
              }}
              className="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
            >
              Envoyez
            </button>
          </div>
        </Form>
        <div className="mt-8 text-center">
          <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Réponse</h1>
          {isLoading ?
            <LuLoaderCircle className="mx-auto my-4 text-indigo-600 animate-spin" size={48} /> :
            <FiCommand className="mx-auto my-4 text-indigo-600" size={48} />}
          {texte.length > 0 && (
            <div className="text-lg text-gray-900 dark:text-white p-4 bg-gray-200 dark:bg-gray-700 rounded-md shadow-md border border-gray-300 dark:border-gray-600">
              {texte}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
