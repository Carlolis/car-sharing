import { HttpServerRequest } from '@effect/platform'
import { pipe, Schema as Sc } from 'effect'
import * as T from 'effect/Effect'
import { Unexpected } from 'effect/ParseResult'
import { Ollama } from 'ollama'

import { useEffect, useState } from 'react'
import { FaCircle } from 'react-icons/fa'
import { FiCommand } from 'react-icons/fi'
import { GiBrain } from 'react-icons/gi'
import { LuLoaderCircle } from 'react-icons/lu'
import { MdOutlineGppBad } from 'react-icons/md'
import { Form, useActionData } from 'react-router'
import { ChatChunk, streamResponse } from '~/contexts/ia.util'
import { Remix } from '~/runtime/Remix'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue
} from '../components/ui/select'

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
  const [selectedModel, setSelectedModel] = useState<string | null>(null)

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
            Demandez à l&apos;IA
          </h2>
        </div>
        <Form className="mt-8 space-y-6" method="post">
          <div className="rounded-md shadow-sm -space-y-px">
            <div className="flex space-x-2">
              <div className="flex-1">
                <Select
                  name="model"
                  onValueChange={value => setSelectedModel(value)}
                >
                  <SelectTrigger
                    id="model"
                    name="model"
                    className="dark:bg-gray-800 dark:text-white"
                  >
                    <SelectValue placeholder="Choisi un modèle" />
                  </SelectTrigger>
                  <SelectContent className="dark:bg-gray-800 dark:text-white">
                    <SelectItem value="codestral:latest">
                      <FaCircle className="inline-block mr-1 text-green-600" />
                      🇫🇷 Mistral Codestral Latest
                    </SelectItem>
                    <SelectItem value="mistral-small:24b">
                      <FaCircle className="inline-block mr-1 text-green-600" />
                      🇫🇷 Mistral Small 3 24B
                    </SelectItem>
                    <SelectItem value="deepseek-coder-v2:latest">
                      <div>
                        <MdOutlineGppBad className="inline-block mr-1 text-red-600" />
                        <FaCircle className="inline-block mr-1 text-green-600" />
                        🇨🇳 DeepSeek Coder V2 Latest
                      </div>
                    </SelectItem>
                    <SelectItem value="deepseek-r1:32b-qwen-distill-q4_K_M">
                      <MdOutlineGppBad className="inline-block mr-1 text-red-600" />
                      <FaCircle className="inline-block mr-1 text-red-600" />
                      <GiBrain className="inline-block mr-1 text-purple-600" />
                      🇨🇳 DeepSeek R1 32B Distill
                    </SelectItem>
                    <SelectItem value="deepseek-r1:14b-qwen-distill-q4_K_M">
                      <MdOutlineGppBad className="inline-block mr-1 text-red-600" />
                      <FaCircle className="inline-block mr-1 text-green-600" />
                      <GiBrain className="inline-block mr-1 text-purple-600" />
                      🇨🇳 DeepSeek R1 14B Distill
                    </SelectItem>
                    <SelectItem value="deepseek-r1:latest">
                      <MdOutlineGppBad className="inline-block mr-1 text-red-600" />
                      <FaCircle className="inline-block mr-1 text-blue-600" />
                      <GiBrain className="inline-block mr-1 text-purple-600" />
                      🇨🇳 DeepSeek R1 Latest
                    </SelectItem>
                    <SelectItem value="llama3.1:8b">
                      <FaCircle className="inline-block mr-1 text-blue-600" />
                      🇺🇸 Llama 3.1
                    </SelectItem>
                    <SelectItem value="llama3.2:3b">
                      <FaCircle className="inline-block mr-1 text-blue-600" />
                      🇺🇸 Llama 3.2
                    </SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </div>
          </div>

          <div className="mt-8 text-center">
            {isLoading ?
              <LuLoaderCircle className="mx-auto my-4 text-indigo-600 animate-spin" size={48} /> :
              <FiCommand className="mx-auto my-4 text-indigo-600" size={48} />}
            {texte.length > 0 && (
              <div className="text-lg text-gray-900 dark:text-white p-4 bg-gray-200 dark:bg-gray-700 rounded-md shadow-md border border-gray-300 dark:border-gray-600">
                {texte}
              </div>
            )}
          </div>
          <div className="flex">
            <input
              id="message"
              name="message"
              type="text"
              required
              className="appearance-none rounded-none w-full px-3 py-2 border border-gray-300 dark:border-gray-600 placeholder-gray-500 dark:placeholder-gray-400 text-gray-900 dark:text-white bg-white dark:bg-gray-800 rounded-l-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
              placeholder="Demandez à l'IA"
            />
            <button
              type="submit"
              onClick={() => {
                setTexte('')
                setIsLoading(true)
              }}
              className={`px-4 py-2 flex items-center justify-center text-white ${
                selectedModel ?
                  'bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500' :
                  'bg-gray-400 cursor-not-allowed'
              } rounded-r-md`}
              disabled={!selectedModel}
            >
              <GiBrain size={20} />
            </button>
          </div>
        </Form>
        <div className="mt-8 text-left">
          <div className="bg-white dark:bg-gray-700 p-4 rounded-md shadow-md border border-gray-300 dark:border-gray-600">
            <h3 className="text-md text-gray-900 dark:text-white">
              Rapidité des modèles
            </h3>
            <ul className="text-sm text-gray-600 dark:text-gray-400 list-disc list-inside">
              <li>
                <FaCircle className="inline-block mr-1 text-red-600" /> très lent
              </li>
              <li>
                <FaCircle className="inline-block mr-1 text-yellow-600" /> lent
              </li>
              <li>
                <FaCircle className="inline-block mr-1 text-green-600" /> rapide
              </li>
              <li>
                <FaCircle className="inline-block mr-1 text-blue-600" /> très rapide
              </li>
            </ul>
            <div className="text-sm text-gray-900 dark:text-white">
              À la première exécution, le lancement peut être lent.
            </div>
          </div>
          <div className="bg-white dark:bg-gray-700 p-4 rounded-md shadow-md border border-gray-300 dark:border-gray-600 mt-4">
            <ul className="text-sm text-gray-600 dark:text-gray-400 list-disc list-inside">
              <li>
                <MdOutlineGppBad className="inline-block mr-1 text-red-600" />
                Modèle censuré. Exemple: Tiananmen en 1989.
              </li>
              <li>
                <GiBrain className="inline-block mr-1 text-purple-600" />{' '}
                Modèle de Raisonnement Avancé (SRA)
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  )
}
