// API配置
const API_CONFIG = {
    BASE_URL: 'http://localhost:1414',
    TIMEOUT: 10000
};

// 创建axios实例
const api = axios.create({
    baseURL: API_CONFIG.BASE_URL,
    timeout: API_CONFIG.TIMEOUT
});

// 请求拦截器 - 添加Token
api.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

// 响应拦截器 - 处理错误
api.interceptors.response.use(
    response => {
        const res = response.data;
        if (res.code === 0) {
            ElMessage.error(res.msg || '操作失败');
            return Promise.reject(new Error(res.msg || '操作失败'));
        }
        return res;
    },
    error => {
        if (error.response) {
            if (error.response.status === 401) {
                // Token过期或未登录
                localStorage.removeItem('token');
                localStorage.removeItem('userInfo');
                window.location.href = './login.html';
            } else {
                ElMessage.error(error.response.data?.msg || '请求失败');
            }
        } else {
            ElMessage.error('网络错误，请检查连接');
        }
        return Promise.reject(error);
    }
);

// API方法
const API = {
    // 用户认证
    login(account, password) {
        return api.post('/login', { username: account, password });
    },
    
    register(userData) {
        return api.post('/register', userData);
    },
    
    checkUsername(username) {
        return api.get(`/register/check-username?username=${username}`);
    },
    
    checkEmail(email) {
        return api.get(`/register/check-email?email=${email}`);
    },
    
    logout() {
        return api.post('/user/logout');
    },
    
    // 用户信息
    getUserInfo() {
        return api.get('/user');
    },
    
    updateUserInfo(data) {
        return api.put('/user', data);
    },
    
    uploadAvatar(file) {
        const formData = new FormData();
        formData.append('avatar', file);
        return api.post('/user/avatar', formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        });
    },
    
    // 商品
    getProducts(params) {
        return api.get('/products', { params });
    },
    
    searchProducts(keyword, page = 1, pageSize = 30) {
        return api.get(`/products/search?keyword=${keyword}&page=${page}&pageSize=${pageSize}`);
    },
    
    getProductDetail(productId) {
        return api.get(`/products/${productId}`);
    },
    
    // 购物车
    addToCart(productId, quantity) {
        return api.post('/cart', { productId, quantity });
    },
    
    getCartList() {
        return api.get('/cart');
    },
    
    updateCartItem(cartId, quantity) {
        return api.put(`/cart/${cartId}`, { quantity });
    },
    
    deleteCartItem(cartId) {
        return api.delete(`/cart/${cartId}`);
    },
    
    // 地址
    getAddressList() {
        return api.get('/addresses');
    },
    
    addAddress(address) {
        return api.post('/addresses', address);
    },
    
    updateAddress(addressId, address) {
        return api.put(`/addresses/${addressId}`, address);
    },
    
    deleteAddress(addressId) {
        return api.delete(`/addresses/${addressId}`);
    },
    
    // 订单
    createOrder(orderData) {
        return api.post('/orders', orderData);
    },
    
    createOrderAndPay(orderData) {
        return api.post('/orders/create-and-pay', orderData);
    },
    
    getOrderList(status) {
        return api.get('/orders', { params: { status } });
    },
    
    getOrderDetail(orderId) {
        return api.get(`/orders/${orderId}`);
    },
    
    cancelOrder(orderId) {
        return api.put(`/orders/${orderId}/cancel`);
    },
    
    confirmReceipt(orderId) {
        return api.put(`/orders/${orderId}/confirm`);
    },
    
    // 支付
    payOrder(orderId, paymentMethod) {
        return api.post('/payment/pay', { orderId, paymentMethod });
    },
    
    getPaymentStatus(orderId) {
        return api.get(`/payment/status/${orderId}`);
    },
    
    // 商家商品管理
    adminGetProducts(params) {
        return api.get('/admin/products', { params });
    },
    
    adminGetProductDetail(productId) {
        return api.get(`/admin/products/${productId}`);
    },
    
    adminAddProduct(product) {
        return api.post('/admin/products', product);
    },
    
    adminUpdateProduct(productId, product) {
        return api.put(`/admin/products/${productId}`, product);
    },
    
    adminDeleteProduct(productId) {
        return api.delete(`/admin/products/${productId}`);
    },
    
    adminUpdateProductStatus(productId, status) {
        return api.put(`/admin/products/${productId}/status`, { status });
    },
    
    adminUpdateProductStock(productId, stock) {
        return api.put(`/admin/products/${productId}/stock`, { stock });
    },
    
    adminUploadMainImage(productId, file) {
        const formData = new FormData();
        formData.append('file', file);
        return api.post(`/admin/products/${productId}/main-image`, formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        });
    },
    
    adminUploadDetailImages(productId, files) {
        const formData = new FormData();
        for (let i = 0; i < files.length; i++) {
            formData.append('files', files[i]);
        }
        return api.post(`/admin/products/${productId}/detail-images`, formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        });
    },
    
    // 商家订单管理
    adminGetOrderList(page = 1, pageSize = 20, status) {
        return api.get('/admin/orders', { params: { page, pageSize, status } });
    },
    
    adminGetOrderDetail(orderId) {
        return api.get(`/admin/orders/${orderId}`);
    },
    
    adminShipOrder(orderId, trackingNumber, shippingCompany) {
        return api.put(`/admin/orders/${orderId}/ship`, { trackingNumber, shippingCompany });
    }
};
