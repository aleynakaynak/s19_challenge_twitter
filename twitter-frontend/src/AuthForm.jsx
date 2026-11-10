import { useState } from 'react'
import { getErrorMessage, loginUser, registerUser } from './api'

export default function AuthForm({ onLogin }) {
  const [register, setRegister] = useState(false)
  const [userName, setUserName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')

  async function handleSubmit(event) {
    event.preventDefault()
    setBusy(true)
    setError('')
    setMessage('')
    try {
      if (register) {
        await registerUser({ userName, email, password })
        setRegister(false)
        setPassword('')
        setMessage('Kayıt oluşturuldu. Şifrenizle giriş yapabilirsiniz.')
      } else {
        const user = await loginUser(userName, password)
        setPassword('')
        onLogin(user)
      }
    } catch (err) {
      setError(getErrorMessage(err))
    } finally {
      setBusy(false)
    }
  }

  function changeMode() {
    setRegister(!register)
    setError('')
    setMessage('')
    setPassword('')
  }

  return (
    <section className="card" aria-labelledby="auth-title">
      <h2 id="auth-title">{register ? 'Kayıt ol' : 'Giriş yap'}</h2>
      <form onSubmit={handleSubmit}>
        <label htmlFor="username">Kullanıcı adı</label>
        <input id="username" required minLength={3} maxLength={30}
          autoComplete="username" value={userName}
          onChange={(event) => setUserName(event.target.value)} disabled={busy} />
        {register && <>
          <label htmlFor="email">E-posta</label>
          <input id="email" type="email" required autoComplete="email"
            value={email} onChange={(event) => setEmail(event.target.value)} disabled={busy} />
        </>}
        <label htmlFor="password">Şifre</label>
        <input id="password" type="password" required minLength={register ? 6 : 1}
          autoComplete={register ? 'new-password' : 'current-password'}
          value={password} onChange={(event) => setPassword(event.target.value)} disabled={busy} />
        <button type="submit" disabled={busy}>{busy ? 'Bekleyin…' : register ? 'Kayıt oluştur' : 'Giriş yap'}</button>
      </form>
      <button type="button" className="secondary" onClick={changeMode} disabled={busy}>
        {register ? 'Hesabım var, giriş yap' : 'Hesabım yok, kayıt ol'}
      </button>
      {error && <p role="alert" className="error">{error}</p>}
      {message && <p role="status">{message}</p>}
    </section>
  )
}
