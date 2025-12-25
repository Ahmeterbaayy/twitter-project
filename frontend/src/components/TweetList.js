import React, { useState, useEffect, useCallback } from 'react';
import { tweetAPI } from '../services/api';

function TweetList({ userId, username }) {
  const [tweets, setTweets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [newTweet, setNewTweet] = useState('');
  const [isCreating, setIsCreating] = useState(false);
  const [deletingId, setDeletingId] = useState(null);

  const fetchTweets = useCallback(async () => {
    try {
      setLoading(true);
      const response = await tweetAPI.getTweetsByUserId(userId);
      setTweets(response.data);
      setError('');
    } catch (err) {
      setError('Tweetler yüklenirken bir hata oluştu.');
    } finally {
      setLoading(false);
    }
  }, [userId]);

  useEffect(() => {
    fetchTweets();
  }, [fetchTweets]);

  const handleCreateTweet = async (e) => {
    e.preventDefault();
    if (!newTweet.trim()) return;

    try {
      setIsCreating(true);
      await tweetAPI.createTweet({
        content: newTweet,
        userId: userId
      });
      setNewTweet('');
      fetchTweets();
    } catch (err) {
      alert('Tweet oluşturulurken bir hata oluştu');
    } finally {
      setIsCreating(false);
    }
  };

  const handleDeleteTweet = async (tweetId) => {
    if (!window.confirm('Bu tweeti silmek istediğinizden emin misiniz?')) return;

    try {
      setDeletingId(tweetId);
      await tweetAPI.deleteTweet(tweetId, userId);
      setTweets(tweets.filter(tweet => tweet.id !== tweetId));
    } catch (err) {
      alert('Tweet silinirken bir hata oluştu');
    } finally {
      setDeletingId(null);
    }
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleString('tr-TR', {
      day: '2-digit',
      month: 'long',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  if (loading) {
    return (
      <div className="text-center py-12 animate-fadeIn">
        <div className="inline-block">
          <svg className="animate-spin h-12 w-12 text-twitter-blue" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <p className="mt-4 text-gray-600 font-medium">Tweetler yükleniyor...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto animate-fadeIn">
      <div className="bg-white rounded-lg p-6 mb-6 shadow border border-gray-200">
        <h3 className="text-xl font-bold text-gray-900 mb-4">Tweet gönder</h3>
        <form onSubmit={handleCreateTweet}>
          <textarea
            value={newTweet}
            onChange={(e) => setNewTweet(e.target.value)}
            placeholder="Ne düşünüyorsun?"
            maxLength={280}
            rows={3}
            className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:border-twitter-blue focus:ring-1 focus:ring-twitter-blue transition-all resize-none mb-3"
          />
          <div className="flex justify-between items-center">
            <span className={`text-sm ${newTweet.length > 260 ? 'text-red-500 font-semibold' : 'text-gray-500'}`}>
              {newTweet.length}/280
            </span>
            <button 
              type="submit" 
              disabled={isCreating || !newTweet.trim()}
              className="bg-twitter-blue text-white px-6 py-2 rounded-full font-bold hover:bg-twitter-dark-blue transition-all disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {isCreating ? (
                <span className="flex items-center gap-2">
                  <svg className="animate-spin h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  Gönderiliyor
                </span>
              ) : 'Tweetle'}
            </button>
          </div>
        </form>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-4 animate-slideDown">
          {error}
        </div>
      )}

      <div className="mb-6">
        <div className="flex items-center justify-between mb-4">
          <h3 className="text-xl font-bold text-gray-900">
            Tweetler <span className="text-twitter-blue">({tweets.length})</span>
          </h3>
          {tweets.length > 0 && (
            <button
              onClick={fetchTweets}
              className="text-gray-600 hover:text-twitter-blue transition-colors text-sm font-medium"
              title="Yenile"
            >
              Yenile
            </button>
          )}
        </div>
        
        {tweets.length === 0 ? (
          <div className="text-center py-12 bg-white rounded-lg shadow border border-gray-200 animate-fadeIn">
            <p className="text-xl font-medium text-gray-600 mb-2">Henüz tweet yok</p>
            <p className="text-gray-400">İlk tweetini paylaş!</p>
          </div>
        ) : (
          <div className="space-y-3">
            {tweets.map((tweet, index) => (
              <div 
                key={tweet.id} 
                className="tweet-card bg-white rounded-lg p-5 shadow border border-gray-200 hover:bg-gray-50 animate-fadeIn"
                style={{ animationDelay: `${index * 0.05}s` }}
              >
                <div className="flex justify-between items-start mb-3">
                  <div className="flex items-start gap-3 flex-1">
                    <div className="bg-gray-200 text-gray-700 w-12 h-12 rounded-full flex items-center justify-center font-bold text-lg">
                      {username?.charAt(0).toUpperCase() || 'U'}
                    </div>
                    <div className="flex flex-col gap-1">
                      <div className="flex items-center gap-2">
                        <strong className="text-gray-900 font-bold">{tweet.username || username}</strong>
                        <span className="text-gray-500">·</span>
                        <span className="text-sm text-gray-500">{formatDate(tweet.createdAt)}</span>
                      </div>
                    </div>
                  </div>
                  <button
                    onClick={() => handleDeleteTweet(tweet.id)}
                    disabled={deletingId === tweet.id}
                    className="text-gray-400 hover:text-red-600 hover:bg-red-50 p-2 rounded-full transition-all disabled:opacity-50"
                    title="Sil"
                  >
                    {deletingId === tweet.id ? (
                      <svg className="animate-spin h-5 w-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                        <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                      </svg>
                    ) : (
                      <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                      </svg>
                    )}
                  </button>
                </div>
                
                <div className="text-gray-900 leading-relaxed mb-3 whitespace-pre-wrap pl-15">
                  {tweet.content}
                </div>
                
                <div className="flex gap-12 text-sm text-gray-500 pt-3 border-t border-gray-100">
                  <button className="flex items-center gap-2 hover:text-red-600 transition-colors group">
                    <svg className="w-5 h-5 group-hover:fill-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                    </svg>
                    <span>{tweet.likeCount}</span>
                  </button>
                  <button className="flex items-center gap-2 hover:text-blue-600 transition-colors group">
                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
                    </svg>
                    <span>{tweet.commentCount}</span>
                  </button>
                  <button className="flex items-center gap-2 hover:text-green-600 transition-colors group">
                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                    </svg>
                    <span>{tweet.retweetCount}</span>
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

export default TweetList;
