import React, { createContext, useContext, useState } from 'react'

interface AuthData {
  token: string
  username: string
}
interface AuthContextType {
  token: string | null
  user: string | null
  setAuth: ({ token, username }: AuthData) => void
  logout: () => void
  isAuthenticated: boolean
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

const isClient = typeof window !== 'undefined'

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [token, setToken] = useState<string | null>(() => {
    if (isClient) {
      return localStorage.getItem('token')
    }
    return null
  })

  const [user, setUser] = useState<string | null>(() => {
    if (isClient) {
      const storedUser = localStorage.getItem('user')
      return storedUser ? JSON.parse(storedUser) : null
    }
    return null
  })

  const setAuth = ({ token, username }: AuthData) => {
    setToken(token)
    setUser(username)
    if (isClient) {
      localStorage.setItem('token', token)
      localStorage.setItem('username', username)
    }
  }

  const logout = () => {
    setToken(null)
    setUser(null)
    if (isClient) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }

  const isAuthenticated = !!token

  return (
    <AuthContext.Provider value={{ token, user, setAuth, logout, isAuthenticated }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}
