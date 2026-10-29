import { useState } from 'react'
import TweetList from './TweetList'

export default function App() {
  const [input, setInput] = useState('1')
  const [userId, setUserId] = useState('1')

  const handleSubmit = (e) => {
    e.preventDefault()
    setUserId(input.trim())
  }

  return (
    <main className="container">
      <h1>Twitter Clone</h1>
      <p className="subtitle">
        <code>GET http://localhost:3000/tweet/findByUserId</code> ile kullanıcının tweetleri
      </p>
      <form onSubmit={handleSubmit} className="search">
        <label htmlFor="userId">Kullanıcı ID</label>
        <input
          id="userId"
          type="number"
          min="1"
          value={input}
          onChange={(e) => setInput(e.target.value)}
        />
        <button type="submit">Getir</button>
      </form>
      <TweetList userId={userId} />
    </main>
  )
}
