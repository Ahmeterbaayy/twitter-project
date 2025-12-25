import React, { useState } from 'react';
import { authAPI } from '../services/api';

function Login({ onLogin }) {
  const [isRegister, setIsRegister] = useState(false);
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    bio: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      let response;
      if (isRegister) {
        response = await authAPI.register(formData);
      } else {
        response = await authAPI.login({
          username: formData.username,
          password: formData.password
        });
      }

      localStorage.setItem('token', response.data.token);
      onLogin(response.data.user);
    } catch (err) {
      setError(err.response?.data?.message || 'Bir hata oluştu');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex justify-center items-center min-h-[70vh] animate-fadeIn">
      <div className="bg-white rounded-2xl p-8 shadow-lg w-full max-w-md border border-gray-200">
        <div className="text-center mb-8">
          <svg viewBox="0 0 24 24" className="w-12 h-12 mx-auto mb-4 text-twitter-blue fill-current">
            <path d="M23.643 4.937c-.835.37-1.732.62-2.675.733.962-.576 1.7-1.49 2.048-2.578-.9.534-1.897.922-2.958 1.13-.85-.904-2.06-1.47-3.4-1.47-2.572 0-4.658 2.086-4.658 4.66 0 .364.042.718.12 1.06-3.873-.195-7.304-2.05-9.602-4.868-.4.69-.63 1.49-.63 2.342 0 1.616.823 3.043 2.072 3.878-.764-.025-1.482-.234-2.11-.583v.06c0 2.257 1.605 4.14 3.737 4.568-.392.106-.803.162-1.227.162-.3 0-.593-.028-.877-.082.593 1.85 2.313 3.198 4.352 3.234-1.595 1.25-3.604 1.995-5.786 1.995-.376 0-.747-.022-1.112-.065 2.062 1.323 4.51 2.093 7.14 2.093 8.57 0 13.255-7.098 13.255-13.254 0-.2-.005-.402-.014-.602.91-.658 1.7-1.477 2.323-2.41z"></path>
          </svg>
          <h2 className="text-3xl font-bold text-gray-900 mb-2">
            {isRegister ? 'Twitter\'a Katıl' : 'Twitter\'a Giriş Yap'}
          </h2>
        </div>
        
        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-4 animate-slideDown">
            <span>{error}</span>
          </div>
        )}
        
        <form onSubmit={handleSubmit} className="space-y-5">
          <div className="relative">
            <label className="block mb-2 text-gray-700 text-sm font-medium">
              Kullanıcı Adı
            </label>
            <input
              type="text"
              name="username"
              value={formData.username}
              onChange={handleChange}
              required
              placeholder="Kullanıcı adınız"
              className="w-full px-4 py-3 border border-gray-300 rounded-md focus:outline-none focus:border-twitter-blue focus:ring-1 focus:ring-twitter-blue transition-all"
            />
          </div>

          {isRegister && (
            <>
              <div className="relative animate-slideDown">
                <label className="block mb-2 text-gray-700 text-sm font-medium">
                  Email
                </label>
                <input
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  required
                  placeholder="email@example.com"
                  className="w-full px-4 py-3 border border-gray-300 rounded-md focus:outline-none focus:border-twitter-blue focus:ring-1 focus:ring-twitter-blue transition-all"
                />
              </div>
              <div className="relative animate-slideDown">
                <label className="block mb-2 text-gray-700 text-sm font-medium">
                  Bio (Opsiyonel)
                </label>
                <textarea
                  name="bio"
                  value={formData.bio}
                  onChange={handleChange}
                  rows="3"
                  placeholder="Kendinizden bahsedin..."
                  className="w-full px-4 py-3 border border-gray-300 rounded-md focus:outline-none focus:border-twitter-blue focus:ring-1 focus:ring-twitter-blue transition-all resize-none"
                />
              </div>
            </>
          )}

          <div className="relative">
            <label className="block mb-2 text-gray-700 text-sm font-medium">
              Şifre
            </label>
            <input
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              required
              placeholder="Şifreniz"
              minLength="6"
              className="w-full px-4 py-3 border border-gray-300 rounded-md focus:outline-none focus:border-twitter-blue focus:ring-1 focus:ring-twitter-blue transition-all"
            />
            {isRegister && (
              <p className="text-xs text-gray-500 mt-1">En az 6 karakter olmalıdır</p>
            )}
          </div>

          <button 
            type="submit" 
            disabled={loading}
            className="w-full bg-twitter-blue text-white py-3 rounded-full font-bold hover:bg-twitter-dark-blue transition-all disabled:opacity-60 disabled:cursor-not-allowed mt-6"
          >
            {loading ? (
              <span className="flex items-center justify-center gap-2">
                <svg className="animate-spin h-5 w-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                İşlem yapılıyor...
              </span>
            ) : (
              isRegister ? 'Kayıt Ol' : 'Giriş Yap'
            )}
          </button>
        </form>

        <div className="mt-6 pt-6 border-t border-gray-200 text-center text-gray-600 text-sm">
          {isRegister ? 'Zaten hesabınız var mı? ' : 'Hesabınız yok mu? '}
          <button 
            onClick={() => {
              setIsRegister(!isRegister);
              setError('');
              setFormData({ username: '', email: '', password: '', bio: '' });
            }} 
            className="text-twitter-blue font-semibold hover:underline transition-all"
          >
            {isRegister ? 'Giriş Yap' : 'Kayıt Ol'}
          </button>
        </div>
      </div>
    </div>
  );
}

export default Login;
