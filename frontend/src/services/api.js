const API_BASE = 'http://localhost:8080/api'

function getToken() { return localStorage.getItem('jwt_token') }

function saveAuth(data) {
  localStorage.setItem('jwt_token', data.token)
  localStorage.setItem('user', JSON.stringify({ id: data.userId, fullName: data.fullName, email: data.email, role: data.role }))
}

async function request(endpoint, options = {}) {
  const url = `${API_BASE}${endpoint}`
  const headers = options.headers || {}
  const token = getToken()
  if (token) headers['Authorization'] = `Bearer ${token}`
  if (!(options.body instanceof FormData)) headers['Content-Type'] = 'application/json'
  const res = await fetch(url, { ...options, headers })
  if (res.status === 401) {
    localStorage.removeItem('jwt_token')
    localStorage.removeItem('user')
    throw new Error('Unauthorized')
  }
  if (res.status === 204) return null
  const data = await res.json().catch(() => null)
  if (!res.ok) throw new Error(data?.message || `Request failed (${res.status})`)
  return data
}

export default {
  getToken,
  saveAuth,
  post(endpoint, body) { return request(endpoint, { method: 'POST', body: JSON.stringify(body) }) },
  get(endpoint) { return request(endpoint) },
  put(endpoint, body) { return request(endpoint, { method: 'PUT', body: JSON.stringify(body) }) },
  delete(endpoint) { return request(endpoint, { method: 'DELETE' }) },
  upload(endpoint, formData) { return request(endpoint, { method: 'POST', body: formData, headers: {} }) }
}
