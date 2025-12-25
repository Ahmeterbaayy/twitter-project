import React, { useState, useEffect, useCallback } from 'react';
import { tweetAPI, likeAPI, commentAPI, retweetAPI } from '../services/api';

function TweetList({ userId, username, showAllTweets = false }) {
  const [tweets, setTweets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [newTweet, setNewTweet] = useState('');
  const [isCreating, setIsCreating] = useState(false);
  const [deletingId, setDeletingId] = useState(null);
  const [commentInputs, setCommentInputs] = useState({});
  const [showComments, setShowComments] = useState({});
  const [tweetComments, setTweetComments] = useState({});
  const [loadingComments, setLoadingComments] = useState({});
  const [editingTweetId, setEditingTweetId] = useState(null);
  const [editTweetContent, setEditTweetContent] = useState('');
  const [editingCommentId, setEditingCommentId] = useState(null);
  const [editCommentContent, setEditCommentContent] = useState('');

  const fetchTweets = useCallback(async () => {
    try {
      setLoading(true);
      const response = showAllTweets 
        ? await tweetAPI.getAllTweets()
        : await tweetAPI.getTweetsByUserId(userId);
      setTweets(response.data);
      setError('');
    } catch (err) {
      setError('Tweetler yüklenirken bir hata oluştu.');
    } finally {
      setLoading(false);
    }
  }, [userId, showAllTweets]);

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
      await tweetAPI.deleteTweet(tweetId);
      setTweets(tweets.filter(tweet => tweet.id !== tweetId));
    } catch (err) {
      alert('Tweet silinirken bir hata oluştu');
    } finally {
      setDeletingId(null);
    }
  };

  const handleLike = async (tweetId) => {
    try {
      await likeAPI.likeTweet({ tweetId, userId });
      fetchTweets();
    } catch (err) {
      if (err.response?.status === 400) {
        handleDislike(tweetId);
      } else {
        alert('Like işlemi başarısız oldu');
      }
    }
  };

  const handleDislike = async (tweetId) => {
    try {
      await likeAPI.dislikeTweet({ tweetId, userId });
      fetchTweets();
    } catch (err) {
      alert('Dislike işlemi başarısız oldu');
    }
  };

  const handleRetweet = async (tweetId) => {
    try {
      await retweetAPI.createRetweet({ originalTweetId: tweetId, userId });
      fetchTweets();
      alert('Retweet başarılı!');
    } catch (err) {
      alert(err.response?.data?.message || 'Retweet işlemi başarısız oldu');
    }
  };

  const loadComments = async (tweetId) => {
    try {
      setLoadingComments(prev => ({ ...prev, [tweetId]: true }));
      const response = await commentAPI.getCommentsByTweetId(tweetId);
      setTweetComments(prev => ({ ...prev, [tweetId]: response.data }));
    } catch (err) {
      console.error('Yorumlar yüklenemedi:', err);
    } finally {
      setLoadingComments(prev => ({ ...prev, [tweetId]: false }));
    }
  };

  const toggleComments = async (tweetId) => {
    const newState = !showComments[tweetId];
    setShowComments(prev => ({ ...prev, [tweetId]: newState }));
    
    if (newState && !tweetComments[tweetId]) {
      await loadComments(tweetId);
    }
  };

  const handleAddComment = async (tweetId) => {
    const content = commentInputs[tweetId];
    if (!content?.trim()) return;

    try {
      await commentAPI.createComment({
        content,
        tweetId,
        userId
      });
      setCommentInputs(prev => ({ ...prev, [tweetId]: '' }));
      await loadComments(tweetId);
      fetchTweets();
    } catch (err) {
      alert('Yorum eklenemedi');
    }
  };

  const handleDeleteComment = async (commentId, tweetId) => {
    if (!window.confirm('Bu yorumu silmek istediğinizden emin misiniz?')) return;

    try {
      await commentAPI.deleteComment(commentId);
      await loadComments(tweetId);
      fetchTweets();
    } catch (err) {
      alert('Yorum silinemedi');
    }
  };

  const handleEditTweet = (tweet) => {
    setEditingTweetId(tweet.id);
    setEditTweetContent(tweet.content);
  };

  const handleUpdateTweet = async (tweetId) => {
    if (!editTweetContent.trim()) return;

    try {
      await tweetAPI.updateTweet(tweetId, { content: editTweetContent, userId });
      setEditingTweetId(null);
      setEditTweetContent('');
      fetchTweets();
    } catch (err) {
      alert('Tweet güncellenemedi');
    }
  };

  const handleCancelEditTweet = () => {
    setEditingTweetId(null);
    setEditTweetContent('');
  };

  const handleEditComment = (comment) => {
    setEditingCommentId(comment.id);
    setEditCommentContent(comment.content);
  };

  const handleUpdateComment = async (commentId, tweetId) => {
    if (!editCommentContent.trim()) return;

    try {
      await commentAPI.updateComment(commentId, { content: editCommentContent, tweetId, userId });
      setEditingCommentId(null);
      setEditCommentContent('');
      await loadComments(tweetId);
      fetchTweets();
    } catch (err) {
      alert('Yorum güncellenemedi');
    }
  };

  const handleCancelEditComment = () => {
    setEditingCommentId(null);
    setEditCommentContent('');
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
              {isCreating ? 'Gönderiliyor...' : 'Tweetle'}
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
            {showAllTweets ? 'Tüm Tweetler' : 'Tweetlerim'} <span className="text-twitter-blue">({tweets.length})</span>
          </h3>
          {tweets.length > 0 && (
            <button
              onClick={fetchTweets}
              className="text-gray-600 hover:text-twitter-blue transition-colors text-sm font-medium"
              title="Yenile"
            >
              🔄 Yenile
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
                      {tweet.username?.charAt(0).toUpperCase() || 'U'}
                    </div>
                    <div className="flex flex-col gap-1">
                      <div className="flex items-center gap-2">
                        <strong className="text-gray-900 font-bold">@{tweet.username}</strong>
                        <span className="text-gray-500">·</span>
                        <span className="text-sm text-gray-500">{formatDate(tweet.createdAt)}</span>
                      </div>
                    </div>
                  </div>
                  {tweet.userId === userId && (
                    <div className="flex gap-2">
                      <button
                        onClick={() => handleEditTweet(tweet)}
                        className="text-gray-400 hover:text-blue-600 hover:bg-blue-50 p-2 rounded-full transition-all"
                        title="Düzenle"
                      >
                        ✏️
                      </button>
                      <button
                        onClick={() => handleDeleteTweet(tweet.id)}
                        disabled={deletingId === tweet.id}
                        className="text-gray-400 hover:text-red-600 hover:bg-red-50 p-2 rounded-full transition-all disabled:opacity-50"
                        title="Sil"
                      >
                        {deletingId === tweet.id ? '⏳' : '🗑️'}
                      </button>
                    </div>
                  )}
                </div>
                
                {editingTweetId === tweet.id ? (
                  <div className="mb-3">
                    <textarea
                      value={editTweetContent}
                      onChange={(e) => setEditTweetContent(e.target.value)}
                      maxLength={280}
                      rows={3}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-twitter-blue text-sm"
                    />
                    <div className="flex justify-end gap-2 mt-2">
                      <button
                        onClick={handleCancelEditTweet}
                        className="px-4 py-1 border border-gray-300 text-gray-700 rounded-full text-sm hover:bg-gray-50"
                      >
                        İptal
                      </button>
                      <button
                        onClick={() => handleUpdateTweet(tweet.id)}
                        className="px-4 py-1 bg-twitter-blue text-white rounded-full text-sm hover:bg-twitter-dark-blue"
                      >
                        Güncelle
                      </button>
                    </div>
                  </div>
                ) : (
                  <div className="text-gray-900 leading-relaxed mb-3 whitespace-pre-wrap pl-15">
                    {tweet.content}
                  </div>
                )}
                
                <div className="flex gap-8 text-sm text-gray-500 pt-3 border-t border-gray-100">
                  <button 
                    onClick={() => handleLike(tweet.id)}
                    className="flex items-center gap-2 hover:text-red-600 transition-colors group"
                  >
                    <span className="text-xl">❤️</span>
                    <span>{tweet.likeCount || 0}</span>
                  </button>
                  
                  <button 
                    onClick={() => toggleComments(tweet.id)}
                    className="flex items-center gap-2 hover:text-blue-600 transition-colors group"
                  >
                    <span className="text-xl">💬</span>
                    <span>{tweet.commentCount || 0}</span>
                  </button>
                  
                  <button 
                    onClick={() => handleRetweet(tweet.id)}
                    className="flex items-center gap-2 hover:text-green-600 transition-colors group"
                  >
                    <span className="text-xl">🔁</span>
                    <span>{tweet.retweetCount || 0}</span>
                  </button>
                </div>

                {showComments[tweet.id] && (
                  <div className="mt-4 pt-4 border-t border-gray-200">
                    <div className="mb-3">
                      <div className="flex gap-2">
                        <input
                          type="text"
                          value={commentInputs[tweet.id] || ''}
                          onChange={(e) => setCommentInputs(prev => ({ ...prev, [tweet.id]: e.target.value }))}
                          placeholder="Yorum yaz..."
                          className="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-twitter-blue text-sm"
                          onKeyPress={(e) => {
                            if (e.key === 'Enter') {
                              handleAddComment(tweet.id);
                            }
                          }}
                        />
                        <button
                          onClick={() => handleAddComment(tweet.id)}
                          className="bg-twitter-blue text-white px-4 py-2 rounded-lg font-semibold hover:bg-twitter-dark-blue transition-all text-sm"
                        >
                          Gönder
                        </button>
                      </div>
                    </div>

                    {loadingComments[tweet.id] ? (
                      <div className="text-center py-4">
                        <span className="text-gray-500">Yorumlar yükleniyor...</span>
                      </div>
                    ) : (
                      <div className="space-y-2">
                        {tweetComments[tweet.id]?.map(comment => (
                          <div key={comment.id} className="bg-gray-50 p-3 rounded-lg">
                            <div className="flex justify-between items-start">
                              <div className="flex-1">
                                <div className="flex items-center gap-2 mb-1">
                                  <strong className="text-sm font-semibold">@{comment.username}</strong>
                                  <span className="text-xs text-gray-500">{formatDate(comment.createdAt)}</span>
                                </div>
                                {editingCommentId === comment.id ? (
                                  <div>
                                    <input
                                      type="text"
                                      value={editCommentContent}
                                      onChange={(e) => setEditCommentContent(e.target.value)}
                                      className="w-full px-2 py-1 border border-gray-300 rounded text-sm mb-2"
                                    />
                                    <div className="flex gap-2">
                                      <button
                                        onClick={handleCancelEditComment}
                                        className="px-3 py-1 border border-gray-300 text-gray-700 rounded text-xs hover:bg-gray-100"
                                      >
                                        İptal
                                      </button>
                                      <button
                                        onClick={() => handleUpdateComment(comment.id, tweet.id)}
                                        className="px-3 py-1 bg-twitter-blue text-white rounded text-xs hover:bg-twitter-dark-blue"
                                      >
                                        Güncelle
                                      </button>
                                    </div>
                                  </div>
                                ) : (
                                  <p className="text-sm text-gray-700">{comment.content}</p>
                                )}
                              </div>
                              {comment.userId === userId && editingCommentId !== comment.id && (
                                <div className="flex gap-1 ml-2">
                                  <button
                                    onClick={() => handleEditComment(comment)}
                                    className="text-gray-400 hover:text-blue-600 text-xs"
                                    title="Düzenle"
                                  >
                                    ✏️
                                  </button>
                                  <button
                                    onClick={() => handleDeleteComment(comment.id, tweet.id)}
                                    className="text-gray-400 hover:text-red-600 text-xs"
                                    title="Yorumu sil"
                                  >
                                    🗑️
                                  </button>
                                </div>
                              )}
                            </div>
                          </div>
                        )) || <p className="text-sm text-gray-500 text-center py-2">Henüz yorum yok</p>}
                      </div>
                    )}
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

export default TweetList;
