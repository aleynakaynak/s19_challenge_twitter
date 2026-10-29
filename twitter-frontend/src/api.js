import axios from 'axios'

// Spring Boot API'si 3000 portunda çalışıyor
export const api = axios.create({ baseURL: 'http://localhost:3000' })

export async function fetchTweetsByUserId(userId) {
  const response = await api.get('/tweet/findByUserId', { params: { userId } })
  return response.data
}
