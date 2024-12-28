import { json, redirect } from "@remix-run/node";
import type { ActionFunctionArgs } from "@remix-run/node";
import { Form, useActionData } from "@remix-run/react";
import { useAuth } from "../contexts/AuthContext";
import { api } from "../services/api";

export async function action({ request }: ActionFunctionArgs) {
  
  const formData = await request.formData();
  const username = formData.get("username") as string;
  const password = formData.get("password") as string;

  try {
    const token = await api.login({ username, password });
    console.log("toto",token)
    // In a real app, you'd want to store the token in a cookie
    return redirect("/dashboard", {
      headers: {
        "Set-Cookie": `token=${token}; Path=/; HttpOnly`,
      },
    });
  } catch (error) {
    console.error(error);
    return json({ error: error instanceof Error ? error.message : "Une erreur est survenue" });
  }
}

export default function Login() {
  const actionData = useActionData<typeof action>();
  const { setAuth } = useAuth();

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 dark:bg-gray-900 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900 dark:text-white">
            Connexion
          </h2>
        </div>
        <Form method="post" className="mt-8 space-y-6">
          {actionData?.error && (
            <div className="rounded-md bg-red-50 dark:bg-red-900 p-4">
              <div className="text-sm text-red-700 dark:text-red-200">{actionData.error}</div>
            </div>
          )}
          <div className="rounded-md shadow-sm -space-y-px">
            <div>
              <input
                type="username"
                name="username"
                required
                className="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 dark:border-gray-600 placeholder-gray-500 dark:placeholder-gray-400 text-gray-900 dark:text-white bg-white dark:bg-gray-800 rounded-t-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                placeholder="Login"
              />
            </div>
            <div>
              <input
                type="password"
                name="password"
                required
                className="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 dark:border-gray-600 placeholder-gray-500 dark:placeholder-gray-400 text-gray-900 dark:text-white bg-white dark:bg-gray-800 rounded-b-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                placeholder="Mot de passe"
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
  );
}
