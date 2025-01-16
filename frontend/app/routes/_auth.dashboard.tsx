import { useLoaderData } from "react-router";
import { api } from "../services/api";

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

export async function loader() {
  // const token = request.headers.get("Cookie")?.match(/token=(.*?)(;|$)/)?.[1];
  
  // if (!token) {
  //   throw new Response("Unauthorized", { status: 401 });
  // }

  try {
    const [totalStats, ] = await Promise.all([
      // api.getUserStats(token),
      api.getTotalStats(),
    ]);
   
    return ({  totalStats });
  } catch (error) {
    throw new Response("Error fetching stats", { status: 500, });
  }
}

export default function Dashboard() {
  const {  totalStats } = useLoaderData<typeof loader>();

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div className="py-8">
        <h2 className="text-2xl font-semibold text-gray-900 mb-8">
        Statistiques Globales
        </h2>
        
        <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3">
          <StatsCard
            title="Nombre total de trajets"
            value={totalStats.trips.length}
          />
          <StatsCard
            title="Distance totale (km)"
            value={Math.round(totalStats.totalKilometers)}
          />
          <StatsCard
            title="Passagers transportés"
            value={totalStats.trips.reduce((acc, trip) => acc + trip.drivers.length, 0)}
          />
          <StatsCard
            title="Distance moyenne (km)"
            value={totalStats.trips.length === 0 ? 0:Math.round(totalStats.totalKilometers / totalStats.trips.length)}
          />
          
        </div>

        </div>
     
    </div>
  );
}
