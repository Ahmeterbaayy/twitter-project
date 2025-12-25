import axios from 'axios';

const API_BASE_URL = 'http://localhost:3000';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export const authAPI = {
  login: (credentials) => api.post('/login', credentials),
  register: (userData) => api.post('/register', userData),
};

export const tweetAPI = {
  getAllTweets: () => api.get('/tweet/all'),
  getTweetsByUserId: (userId) => api.get(`/tweet/findByUserId?userId=${userId}`),
  getTweetById: (id) => api.get(`/tweet/findById?id=${id}`),
  createTweet: (tweetData) => api.post('/tweet', tweetData),
  updateTweet: (id, tweetData) => api.put(`/tweet/${id}`, tweetData),
  deleteTweet: (id) => api.delete(`/tweet/${id}`),
};

export const commentAPI = {
  createComment: (commentData) => api.post('/comment', commentData),
  updateComment: (id, commentData) => api.put(`/comment/${id}`, commentData),
  deleteComment: (id) => api.delete(`/comment/${id}`),
  getCommentsByTweetId: (tweetId) => api.get(`/comment/tweet/${tweetId}`),
};

export const likeAPI = {
  likeTweet: (likeData) => api.post('/like', likeData),
  dislikeTweet: (likeData) => api.post('/dislike', likeData),
};

export const retweetAPI = {
  createRetweet: (retweetData) => api.post('/retweet', retweetData),
  deleteRetweet: (id) => api.delete(`/retweet/${id}`),
};

export default api;
