import { json, redirect, ActionFunctionArgs } from "@remix-run/node";
import { Form, useActionData } from "@remix-run/react";
import { api } from "~/services/api";

export async function action({ request }: ActionFunctionArgs) {
  const token = request.headers.get("Cookie")?.match(/token=(.*?)(;|$)/)?.[1];
  
  if (!token) {
    throw new Response("Unauthorized", { status: 401 });
  }

  const formData = await request.formData();
  const tripData = {
    startLocation: formData.get("startLocation") as string,
    endLocation: formData.get("endLocation") as string,
    date: formData.get("date") as string,
    distance: Number(formData.get("distance")),
    passengers: Number(formData.get("passengers")),
  };

  try {
    await api.createTrip(tripData, token);
    return redirect("/dashboard");
  } catch (error) {
    return json({ error: error instanceof Error ? error.message : "Une erreur est survenue" });
  }
}

export default function CreateTrip() {
  const actionData = useActionData<typeof action>();

  return (
    <div className="max-w-md mx-auto mt-10 px-4">
      <h2 className="text-2xl font-bold mb-6">Créer un nouveau trajet</h2>
      
      {actionData?.error && (
        <div className="mb-4 p-4 text-red-700 bg-red-100 rounded">
          {actionData.error}
        </div>
      )}

      <Form method="post" className="space-y-6">
        <div>
          <label className="block text-sm font-medium text-gray-700">
            Lieu de départ
          </label>
          <input
            type="text"
            name="startLocation"
            required
            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">
            Lieu d'arrivée
          </label>
          <input
            type="text"
            name="endLocation"
            required
            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">
            Date
          </label>
          <input
            type="date"
            name="date"
            required
            defaultValue={new Date().toISOString().split("T")[0]}
            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">
            Distance (km)
          </label>
          <input
            type="number"
            name="distance"
            required
            min="0"
            step="0.1"
            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">
            Nombre de passagers
          </label>
          <input
            type="number"
            name="passengers"
            required
            min="1"
            defaultValue={1}
            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
          />
        </div>

        <button
          type="submit"
          className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
        >
          Créer le trajet
        </button>
      </Form>
    </div>
  );
}
