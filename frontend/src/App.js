import React, { useState } from 'react';
import TweetList from './components/TweetList';
import Login from './components/Login';

function App() {
  const [user, setUser] = useState(null);
  const [view, setView] = useState('feed'); // 'feed' veya 'myTweets'

  const handleLogin = (userData) => {
    setUser(userData);
  };

  const handleLogout = () => {
    setUser(null);
    localStorage.removeItem('token');
  };

  return (
    <div className="min-h-screen bg-white">
      <header className="bg-white border-b border-gray-200 px-4 sm:px-8 py-3 flex justify-between items-center sticky top-0 z-50 backdrop-blur-sm bg-opacity-95">
        <div className="flex items-center gap-3 animate-fadeIn">
          <svg viewBox="0 0 24 24" className="w-8 h-8 text-twitter-blue fill-current">
            <path d="M23.643 4.937c-.835.37-1.732.62-2.675.733.962-.576 1.7-1.49 2.048-2.578-.9.534-1.897.922-2.958 1.13-.85-.904-2.06-1.47-3.4-1.47-2.572 0-4.658 2.086-4.658 4.66 0 .364.042.718.12 1.06-3.873-.195-7.304-2.05-9.602-4.868-.4.69-.63 1.49-.63 2.342 0 1.616.823 3.043 2.072 3.878-.764-.025-1.482-.234-2.11-.583v.06c0 2.257 1.605 4.14 3.737 4.568-.392.106-.803.162-1.227.162-.3 0-.593-.028-.877-.082.593 1.85 2.313 3.198 4.352 3.234-1.595 1.25-3.604 1.995-5.786 1.995-.376 0-.747-.022-1.112-.065 2.062 1.323 4.51 2.093 7.14 2.093 8.57 0 13.255-7.098 13.255-13.254 0-.2-.005-.402-.014-.602.91-.658 1.7-1.477 2.323-2.41z"></path>
          </svg>
          <h1 className="text-xl sm:text-2xl font-bold text-gray-900">Twitter</h1>
        </div>
        {user && (
          <div className="flex items-center gap-2 sm:gap-4 animate-slideDown">
            <div className="hidden sm:flex items-center gap-2 text-gray-700">
              <span className="font-semibold">{user.username}</span>
            </div>
            <div className="flex gap-2">
              <button 
                onClick={() => setView('feed')}
                className={`px-4 py-2 rounded-full font-semibold transition-all ${
                  view === 'feed' 
                    ? 'bg-twitter-blue text-white' 
                    : 'border border-gray-300 text-gray-700 hover:bg-gray-50'
                }`}
              >
                Ana Sayfa
              </button>
              <button 
                onClick={() => setView('myTweets')}
                className={`px-4 py-2 rounded-full font-semibold transition-all ${
                  view === 'myTweets' 
                    ? 'bg-twitter-blue text-white' 
                    : 'border border-gray-300 text-gray-700 hover:bg-gray-50'
                }`}
              >
                Tweetlerim
              </button>
              <button 
                onClick={handleLogout} 
                className="border border-gray-300 text-gray-700 px-4 py-2 rounded-full font-semibold hover:bg-gray-50 transition-all"
              >
                Çıkış
              </button>
            </div>
          </div>
        )}
      </header>

      <main className="max-w-2xl mx-auto my-8 px-4 animate-fadeIn">
        {user ? (
          <TweetList 
            userId={user.id} 
            username={user.username} 
            showAllTweets={view === 'feed'}
          />
        ) : (
          <Login onLogin={handleLogin} />
        )}
      </main>
    </div>
  );
}

export default App;
