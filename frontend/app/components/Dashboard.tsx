import React, { useEffect, useState } from 'react';
import { api } from '../services/api';
import { useAuth } from '../contexts/AuthContext';
import { TripStats } from '../types/api';

function StatsCard({ title, value }: { title: string; value: string | number }) {
  return (
    <div className="bg-white overflow-hidden shadow rounded-lg">
      <div className="px-4 py-5 sm:p-6">
        <dt className="text-sm font-medium text-gray-500 truncate">{title}</dt>
        <dd className="mt-1 text-3xl font-semibold text-gray-900">{value}</dd>
      </div>
    </div>
  );
}

export function Dashboard() {
  const { token } = useAuth();
  const [userStats, setUserStats] = useState<TripStats | null>(null);
  const [totalStats, setTotalStats] = useState<TripStats | null>(null);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchStats = async () => {
      try {
        if (token) {
          const [userStatsData, totalStatsData] = await Promise.all([
            api.getUserStats(token),
            api.getTotalStats(),
          ]);
          setUserStats(userStatsData);
          setTotalStats(totalStatsData);
        }
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Une erreur est survenue');
      }
    };

    fetchStats();
  }, [token]);

  if (error) {
    return (
      <div className="rounded-md bg-red-50 p-4 m-4">
        <div className="text-sm text-red-700">{error}</div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div className="py-8">
        <h2 className="text-2xl font-semibold text-gray-900 mb-8">
          Mes Statistiques
        </h2>
        
        {userStats && (
          <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3">
            <StatsCard
              title="Nombre total de trajets"
              value={userStats.totalTrips}
            />
            <StatsCard
              title="Distance totale (km)"
              value={Math.round(userStats.totalDistance)}
            />
            <StatsCard
              title="Passagers transportés"
              value={userStats.totalPassengers}
            />
            <StatsCard
              title="Distance moyenne (km)"
              value={Math.round(userStats.averageDistance)}
            />
            <StatsCard
              title="Moyenne de passagers"
              value={userStats.averagePassengers.toFixed(1)}
            />
          </div>
        )}

        <h2 className="text-2xl font-semibold text-gray-900 mt-12 mb-8">
          Statistiques Globales
        </h2>
        
        {totalStats && (
          <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3">
            <StatsCard
              title="Nombre total de trajets"
              value={totalStats.totalTrips}
            />
            <StatsCard
              title="Distance totale (km)"
              value={Math.round(totalStats.totalDistance)}
            />
            <StatsCard
              title="Passagers transportés"
              value={totalStats.totalPassengers}
            />
          </div>
        )}
      </div>
    </div>
  );
}
