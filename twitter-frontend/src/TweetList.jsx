import { useEffect, useState } from 'react'
import { fetchTweetsByUserId, getErrorMessage, likeTweet } from './api'

export default function TweetList({ userId, refresh, canLike, onRefresh }) {
  const [tweets, setTweets] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [actionError, setActionError] = useState('')
  const [liking, setLiking] = useState(null)

  useEffect(() => {
    let active = true
    setActionError('')
    if (!userId) return
    setLoading(true)
    setError('')
    fetchTweetsByUserId(userId)
      .then((data) => { if (active) setTweets(data) })
      .catch((err) => {
        if (active) { setTweets([]); setError(getErrorMessage(err)) }
      })
      .finally(() => { if (active) setLoading(false) })
    return () => { active = false }
  }, [userId, refresh])

  async function handleLike(tweetId) {
    setLiking(tweetId)
    setActionError('')
    try {
      await likeTweet(tweetId)
      onRefresh()
    } catch (err) {
      setActionError(getErrorMessage(err))
    } finally {
      setLiking(null)
    }
  }

  if (!userId) return <p>Kullanıcı ID girerek tweetleri listeleyebilirsiniz.</p>
  if (loading) return <p role="status">Yükleniyor…</p>
  if (error) return <p role="alert" className="error">{error}</p>

  return <>
    {actionError && <p role="alert" className="error">{actionError}</p>}
    {tweets.length === 0 && <p>Bu kullanıcının henüz tweeti yok.</p>}
    <ul className="tweets">
      {tweets.map((tweet) => <li key={tweet.id} className="tweet">
        <div className="tweet-head"><strong>@{tweet.userName}</strong>
          <time>{tweet.createdAt ? new Date(tweet.createdAt).toLocaleString('tr-TR') : ''}</time></div>
        <p className="tweet-content">{tweet.content}</p>
        <div className="tweet-stats">
          <span>Yorum: {tweet.commentCount}</span><span>Retweet: {tweet.retweetCount}</span>
          <button type="button" className="secondary" disabled={!canLike || liking !== null}
            onClick={() => handleLike(tweet.id)} aria-label={`Tweet ${tweet.id} beğen`}>
            {liking === tweet.id ? 'Bekleyin…' : `♡ Beğen (${tweet.likeCount})`}
          </button>
        </div>
      </li>)}
    </ul>
    {!canLike && <p className="hint">Tweet paylaşmak ve beğenmek için giriş yapın.</p>}
  </>
}
