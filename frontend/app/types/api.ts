export interface User {
  id: string;
  email: string;
  name: string;
}

export interface UserCreate {
  email: string;
  password: string;
  name: string;
}

export interface UserLogin {
  email: string;
  password: string;
}

export interface Trip {
  id: string;
  userId: string;
  startLocation: string;
  endLocation: string;
  date: string;
  distance: number;
  passengers: number;
}

export interface TripCreate {
  startLocation: string;
  endLocation: string;
  date: string;
  distance: number;
  passengers: number;
}

export interface TripStats {
  totalTrips: number;
  totalDistance: number;
  totalPassengers: number;
  averageDistance: number;
  averagePassengers: number;
}

export interface ErrorResponse {
  message: string;
}
