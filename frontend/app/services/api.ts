import { User, UserCreate, UserLogin, Trip, TripCreate, TripStats } from '../types/api';

const API_URL = 'http://localhost:8080/api';

export class ApiError extends Error {
  constructor(public statusCode: number, message: string) {
    super(message);
  }
}

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const error = await response.json();
    throw new ApiError(response.status, error.message);
  }
  return response.json();
}

export const api = {
  async register(user: UserCreate): Promise<User> {
    const response = await fetch(`${API_URL}/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(user),
    });
    return handleResponse<User>(response);
  },

  async login(credentials: UserLogin): Promise<string> {
    
    const response = await fetch(`${API_URL}/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(credentials),
    });
    const responseData =await response.clone().json()
    
    return handleResponse<string>(responseData);
  },

  async createTrip(trip: TripCreate, token: string): Promise<Trip> {
    const response = await fetch(`${API_URL}/trips`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
      body: JSON.stringify(trip),
    });
    return handleResponse<Trip>(response);
  },

  async getUserStats(token: string): Promise<TripStats> {
    const response = await fetch(`${API_URL}/trips/user`, {
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    });
    return handleResponse<TripStats>(response);
  },

  async getTotalStats(): Promise<TripStats> {
    const response = await fetch(`${API_URL}/trips/total`);
    return handleResponse<TripStats>(response);
  },
};
