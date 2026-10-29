import { useEffect, useState } from 'react'
import { fetchTweetsByUserId } from './api'

function formatDate(value) {
  if (!value) return ''
  return new Date(value).toLocaleString('tr-TR')
}

export default function TweetList({ userId }) {
  const [tweets, setTweets] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  useEffect(() => {
    if (!userId) return
    setLoading(true)
    setError(null)
    fetchTweetsByUserId(userId)
      .then(setTweets)
      .catch((err) => {
        setTweets([])
        if (err.response) {
          // Sunucu cevap verdi ama hata döndü (ör. 404 kullanıcı yok)
          setError(err.response.data?.message || `Hata: ${err.response.status}`)
        } else {
          // Cevap hiç okunamadı: CORS engeli ya da API kapalı
          setError(
            'API\'ye ulaşılamadı. Backend 3000 portunda çalışıyor mu? Çalışıyorsa tarayıcı konsolunda CORS hatası olabilir.'
          )
        }
      })
      .finally(() => setLoading(false))
  }, [userId])

  if (loading) return <p className="info">Yükleniyor...</p>
  if (error) return <p className="error">{error}</p>
  if (tweets.length === 0) return <p className="info">Bu kullanıcının henüz tweeti yok.</p>

  return (
    <ul className="tweets">
      {tweets.map((tweet) => (
        <li key={tweet.id} className="tweet">
          <div className="tweet-head">
            <strong>@{tweet.userName}</strong>
            <span>{formatDate(tweet.createdAt)}</span>
          </div>
          <p>{tweet.content}</p>
          <div className="tweet-stats">
            <span>💬 {tweet.commentCount}</span>
            <span>🔁 {tweet.retweetCount}</span>
            <span>❤️ {tweet.likeCount}</span>
          </div>
        </li>
      ))}
    </ul>
  )
}
