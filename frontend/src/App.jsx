import React from 'react'
import Login from './pages/Login'
import Jobs from './pages/Jobs'
import api from './services/api'

export default function App() {
  const isAuth = !!api.getToken()
  return (
    <div>
      {isAuth ? <Jobs /> : <Login />}
    </div>
  )
}
