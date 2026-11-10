import axios from 'axios'

export const api = axios.create({ baseURL: 'http://localhost:3000', timeout: 15000 })

// Bilgiler sadece açık sekmenin belleğinde tutulur; yenileyince tekrar giriş gerekir.
let authorization = null

export function clearAuth() {
  authorization = null
}

function authConfig() {
  return { headers: { Authorization: authorization } }
}

export async function registerUser(values) {
  const response = await api.post('/register', values)
  return response.data
}

export async function loginUser(userName, password) {
  const response = await api.post('/login', { userName, password })
  const bytes = new TextEncoder().encode(`${userName}:${password}`)
  authorization = `Basic ${btoa(String.fromCharCode(...bytes))}`
  return response.data.user
}

export async function fetchTweetsByUserId(userId) {
  const response = await api.get('/tweet/findByUserId', { params: { userId } })
  return response.data
}

export async function createTweet(content) {
  const response = await api.post('/tweet', { content }, authConfig())
  return response.data
}

export async function likeTweet(tweetId) {
  const response = await api.post('/like', { tweetId }, authConfig())
  return response.data
}

export function getErrorMessage(error) {
  if (error.response) {
    return error.response.data?.message || `İşlem başarısız (${error.response.status}).`
  }
  return 'API’ye ulaşılamadı. Backend bağlantısını ve CORS ayarlarını kontrol edin.'
}
