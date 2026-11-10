import { useState } from 'react'
import AuthForm from './AuthForm'
import TweetList from './TweetList'
import { clearAuth, createTweet, getErrorMessage } from './api'

export default function App() {
  const [user, setUser] = useState(null)
  const [input, setInput] = useState('')
  const [userId, setUserId] = useState('')
  const [refresh, setRefresh] = useState(0)
  const [content, setContent] = useState('')
  const [posting, setPosting] = useState(false)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')

  function handleLogin(loggedInUser) {
    setUser(loggedInUser)
    setInput(String(loggedInUser.id))
    setUserId(String(loggedInUser.id))
    setRefresh((value) => value + 1)
    setError('')
    setMessage('')
  }

  function handleLogout() {
    clearAuth()
    setUser(null)
    setContent('')
    setMessage('')
    setError('')
  }

  function handleSearch(event) {
    event.preventDefault()
    setUserId(input)
    setRefresh((value) => value + 1)
  }

  async function handleCreate(event) {
    event.preventDefault()
    if (!content.trim()) return
    setPosting(true)
    setError('')
    setMessage('')
    try {
      await createTweet(content.trim())
      setContent('')
      setInput(String(user.id))
      setUserId(String(user.id))
      setRefresh((value) => value + 1)
      setMessage('Tweet paylaşıldı.')
    } catch (err) {
      setError(getErrorMessage(err))
    } finally {
      setPosting(false)
    }
  }

  return (
    <main className="container">
      <header><h1>Twitter Clone</h1><p>Tweet paylaş ve kullanıcıların tweetlerini görüntüle.</p></header>
      {!user ? <AuthForm onLogin={handleLogin} /> : <section className="card">
        <div className="session"><p><strong>@{user.userName}</strong> · Kullanıcı ID: {user.id}</p>
          <button type="button" className="secondary" onClick={handleLogout} disabled={posting}>Çıkış yap</button></div>
        <form onSubmit={handleCreate}>
          <label htmlFor="tweet-content">Ne paylaşmak istersin?</label>
          <textarea id="tweet-content" required maxLength={280} rows={3} value={content}
            onChange={(event) => setContent(event.target.value)} disabled={posting} />
          <div className="session"><small>{content.length}/280</small>
            <button type="submit" disabled={posting || !content.trim()}>{posting ? 'Paylaşılıyor…' : 'Tweet paylaş'}</button></div>
        </form>
        {error && <p role="alert" className="error">{error}</p>}
        {message && <p role="status">{message}</p>}
      </section>}
      <section className="card" aria-labelledby="tweets-title">
        <h2 id="tweets-title">Tweetler</h2>
        <form onSubmit={handleSearch} className="search">
          <label htmlFor="userId">Kullanıcı ID</label>
          <input id="userId" type="number" min="1" step="1" required value={input}
            onChange={(event) => setInput(event.target.value)} />
          <button type="submit">Getir</button>
        </form>
        <TweetList userId={userId} refresh={refresh} canLike={Boolean(user)}
          onRefresh={() => setRefresh((value) => value + 1)} />
      </section>
    </main>
  )
}
