import {
  Links,
  Meta,
  Outlet,
  Scripts,
  ScrollRestoration,
  Link,
} from "@remix-run/react";
import type { LinksFunction } from "@remix-run/node";
import { AuthProvider, useAuth } from "./contexts/AuthContext";

import stylesheet from "./tailwind.css?url";

export const links: LinksFunction = () => [
  { rel: "preconnect", href: "https://fonts.googleapis.com" },
  { rel: "stylesheet", href: stylesheet },
  {
    rel: "preconnect",
    href: "https://fonts.gstatic.com",
    crossOrigin: "anonymous",
  },
  {
    rel: "stylesheet",
    href: "https://fonts.googleapis.com/css2?family=Inter:ital,opsz,wght@0,14..32,100..900;1,14..32,100..900&display=swap",
  },
];

function Navigation() {
  const { isAuthenticated } = useAuth();

  return (
    <nav className="bg-gray-800">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <Link to="/" className="text-white font-bold text-xl">
                Car Share
              </Link>
            </div>
            {isAuthenticated && (
              <div className="ml-10 flex items-baseline space-x-4">
                <Link
                  to="/dashboard"
                  className="text-gray-300 hover:bg-gray-700 hover:text-white px-3 py-2 rounded-md text-sm font-medium"
                >
                  Tableau de bord
                </Link>
                <Link
                  to="/trips/new"
                  className="text-gray-300 hover:bg-gray-700 hover:text-white px-3 py-2 rounded-md text-sm font-medium"
                >
                  Nouveau trajet
                </Link>
              </div>
            )}
          </div>
          <div className="flex items-center">
            {isAuthenticated ? (
              <form action="/logout" method="post">
                <button
                  type="submit"
                  className="text-gray-300 hover:bg-gray-700 hover:text-white px-3 py-2 rounded-md text-sm font-medium"
                >
                  Déconnexion
                </button>
              </form>
            ) : null
            }
          </div>
        </div>
      </div>
    </nav>
  );
}

export function Layout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="fr">
      <head>
        <meta charSet="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <Meta />
        <Links />
      </head>
      <body className="min-h-screen bg-gray-100">
       
        {children}
        <ScrollRestoration />
        <Scripts />
      </body>
    </html>
  );
}

export default function App() {
  return (
    <Layout>
      <AuthProvider>
        <Navigation />
        <Outlet />
      </AuthProvider>
    </Layout>
  );
}
