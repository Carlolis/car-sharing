import React, { useState } from 'react';
import { Form, useActionData } from '@remix-run/react';
import { api } from '../services/api';
import { useAuth } from '../contexts/AuthContext';

export function CreateTrip() {
  const [error, setError] = useState('');
  const { token } = useAuth();
  const actionData = useActionData();

  return (
    <div className="max-w-md mx-auto mt-10 px-4">
      <h2 className="text-2xl font-bold mb-6">Créer un nouveau trajet</h2>
      
      {error && (
        <div className="mb-4 p-4 text-red-700 bg-red-100 rounded">
          {error}
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
            defaultValue={new Date().toISOString().split('T')[0]}
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
