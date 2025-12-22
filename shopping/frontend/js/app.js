const { createApp } = Vue;
const { createRouter, createWebHashHistory } = VueRouter;

// API基础配置
const API_BASE = 'http://localhost:1414';
const axiosInstance = axios.create({
    baseURL: API_BASE,
    timeout: 10000
});

// 请求拦截器 - 添加Token
axiosInstance.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.token = token;
        }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

// 响应拦截器 - 处理错误
axiosInstance.interceptors.response.use(
    response => {
        if (response.data.code === 0) {
            ElMessage.error(response.data.msg || '操作失败');
        }
        return response;
    },
    error => {
        if (error.response && error.response.status === 401) {
            localStorage.removeItem('token');
            localStorage.removeItem('userInfo');
            ElMessage.error('登录已过期，请重新登录');
            window.location.hash = '#/';
        }
        return Promise.reject(error);
    }
);

// 首页组件
const Home = {
    template: `
        <div>
            <el-card>
                <template #header>
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <span>商品列表</span>
                        <div style="display: flex; gap: 10px;">
                            <el-input-number v-model="filters.minPrice" placeholder="最低价" :min="0" style="width: 120px;"></el-input-number>
                            <span>-</span>
                            <el-input-number v-model="filters.maxPrice" placeholder="最高价" :min="0" style="width: 120px;"></el-input-number>
                            <el-select v-model="filters.sort" placeholder="排序" style="width: 150px;">
                                <el-option label="默认" value="default"></el-option>
                                <el-option label="价格低到高" value="price_asc"></el-option>
                                <el-option label="价格高到低" value="price_desc"></el-option>
                            </el-select>
                            <el-button type="primary" @click="loadProducts">筛选</el-button>
                        </div>
                    </div>
                </template>
                <div v-if="loading" style="text-align: center; padding: 40px;">
                    <el-icon class="is-loading" style="font-size: 40px;"><Loading /></el-icon>
                </div>
                <div v-else-if="products.length === 0" style="text-align: center; padding: 40px; color: #909399;">
                    暂无商品
                </div>
                <div v-else class="product-grid">
                    <div v-for="product in products" :key="product.productId" class="product-card" @click="goToDetail(product.productId)">
                        <img :src="product.mainImage || '/default-product.jpg'" :alt="product.name" class="product-image" @error="handleImageError">
                        <div class="product-info">
                            <div class="product-name">{{ product.name }}</div>
                            <div class="product-price">¥{{ product.price }}</div>
                            <div style="font-size: 12px; color: #909399; margin-top: 5px;">
                                库存: {{ product.stockStatus }}
                            </div>
                        </div>
                    </div>
                </div>
                <el-pagination
                    v-model:current-page="pagination.page"
                    v-model:page-size="pagination.pageSize"
                    :total="pagination.total"
                    :page-sizes="[12, 24, 48]"
                    layout="total, sizes, prev, pager, next, jumper"
                    @size-change="loadProducts"
                    @current-change="loadProducts"
                    style="margin-top: 20px; justify-content: center;">
                </el-pagination>
            </el-card>
        </div>
    `,
    data() {
        return {
            products: [],
            loading: false,
            filters: {
                minPrice: null,
                maxPrice: null,
                sort: 'default'
            },
            pagination: {
                page: 1,
                pageSize: 12,
                total: 0
            }
        };
    },
    mounted() {
        this.loadProducts();
    },
    methods: {
        async loadProducts() {
            this.loading = true;
            try {
                const params = {
                    page: this.pagination.page,
                    pageSize: this.pagination.pageSize,
                    minPrice: this.filters.minPrice,
                    maxPrice: this.filters.maxPrice,
                    sort: this.filters.sort
                };
                const response = await axiosInstance.get('/products', { params });
                if (response.data.code === 1) {
                    this.products = response.data.data.list || [];
                    this.pagination.total = response.data.data.total || 0;
                }
            } catch (error) {
                console.error('加载商品失败:', error);
            } finally {
                this.loading = false;
            }
        },
        goToDetail(productId) {
            this.$router.push(`/product/${productId}`);
        },
        handleImageError(e) {
            e.target.src = 'https://via.placeholder.com/250x200?text=No+Image';
        }
    }
};

// 商品详情页组件
const ProductDetail = {
    template: `
        <div>
            <el-card v-if="product">
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-image :src="product.images && product.images[0]" fit="contain" style="width: 100%; height: 500px;"></el-image>
                    </el-col>
                    <el-col :span="12">
                        <h2>{{ product.name }}</h2>
                        <div style="font-size: 28px; color: #f56c6c; margin: 20px 0;">¥{{ product.price }}</div>
                        <div style="margin: 20px 0;">
                            <span>库存: </span>
                            <el-tag :type="product.stock > 10 ? 'success' : 'warning'">{{ product.stock }} 件</el-tag>
                        </div>
                        <div style="margin: 20px 0;">
                            <el-input-number v-model="quantity" :min="1" :max="product.stock" label="数量"></el-input-number>
                        </div>
                        <div style="margin: 20px 0;">
                            <el-button type="danger" size="large" @click="addToCart" :disabled="product.stock === 0">
                                加入购物车
                            </el-button>
                            <el-button type="primary" size="large" @click="buyNow" :disabled="product.stock === 0">
                                立即购买
                            </el-button>
                        </div>
                        <el-divider></el-divider>
                        <div v-html="product.description"></div>
                    </el-col>
                </el-row>
            </el-card>
            <div v-else style="text-align: center; padding: 40px;">
                <el-icon class="is-loading" style="font-size: 40px;"><Loading /></el-icon>
            </div>
        </div>
    `,
    data() {
        return {
            product: null,
            quantity: 1
        };
    },
    mounted() {
        this.loadProduct();
    },
    methods: {
        async loadProduct() {
            const productId = this.$route.params.id;
            try {
                const response = await axiosInstance.get(`/products/${productId}`);
                if (response.data.code === 1) {
                    this.product = response.data.data;
                } else {
                    ElMessage.error('商品不存在');
                    this.$router.push('/');
                }
            } catch (error) {
                ElMessage.error('加载商品失败');
            }
        },
        async addToCart() {
            if (!localStorage.getItem('token')) {
                ElMessage.warning('请先登录');
                this.$root.showLogin = true;
                return;
            }
            try {
                const response = await axiosInstance.post('/cart', {
                    productId: this.product.productId,
                    quantity: this.quantity
                });
                if (response.data.code === 1) {
                    ElMessage.success('已加入购物车');
                    this.$root.loadCartCount();
                }
            } catch (error) {
                ElMessage.error('加入购物车失败');
            }
        },
        buyNow() {
            if (!localStorage.getItem('token')) {
                ElMessage.warning('请先登录');
                this.$root.showLogin = true;
                return;
            }
            // 跳转到订单确认页
            this.$router.push(`/checkout?productId=${this.product.productId}&quantity=${this.quantity}`);
        }
    }
};

// 购物车页面组件
const Cart = {
    template: `
        <div>
            <el-card>
                <template #header>
                    <span>购物车</span>
                </template>
                <div v-if="cartItems.length === 0" class="empty-cart">
                    <div class="empty-cart-icon">🛒</div>
                    <p style="color: #909399; margin-bottom: 20px;">购物车是空的</p>
                    <el-button type="primary" @click="$router.push('/')">去逛逛</el-button>
                </div>
                <div v-else>
                    <el-table :data="cartItems" style="width: 100%">
                        <el-table-column type="selection" width="55"></el-table-column>
                        <el-table-column label="商品" width="300">
                            <template #default="{ row }">
                                <div style="display: flex; align-items: center;">
                                    <img :src="row.mainImage" style="width: 80px; height: 80px; object-fit: cover; margin-right: 10px;">
                                    <span>{{ row.productName }}</span>
                                </div>
                            </template>
                        </el-table-column>
                        <el-table-column label="单价" prop="price" width="120">
                            <template #default="{ row }">¥{{ row.price }}</template>
                        </el-table-column>
                        <el-table-column label="数量" width="150">
                            <template #default="{ row }">
                                <el-input-number v-model="row.quantity" :min="1" :max="row.stock" @change="updateCartItem(row)"></el-input-number>
                            </template>
                        </el-table-column>
                        <el-table-column label="小计" width="120">
                            <template #default="{ row }">¥{{ (row.price * row.quantity).toFixed(2) }}</template>
                        </el-table-column>
                        <el-table-column label="操作" width="100">
                            <template #default="{ row }">
                                <el-button type="danger" size="small" @click="deleteCartItem(row)">删除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <div style="margin-top: 20px; text-align: right;">
                        <div style="font-size: 20px; margin-bottom: 20px;">
                            总计: <span style="color: #f56c6c; font-weight: bold;">¥{{ totalPrice.toFixed(2) }}</span>
                        </div>
                        <el-button type="primary" size="large" @click="checkout">去结算</el-button>
                    </div>
                </div>
            </el-card>
        </div>
    `,
    data() {
        return {
            cartItems: []
        };
    },
    computed: {
        totalPrice() {
            return this.cartItems.reduce((sum, item) => sum + item.price * item.quantity, 0);
        }
    },
    mounted() {
        this.loadCart();
    },
    methods: {
        async loadCart() {
            try {
                const response = await axiosInstance.get('/cart');
                if (response.data.code === 1) {
                    this.cartItems = response.data.data || [];
                }
            } catch (error) {
                console.error('加载购物车失败:', error);
            }
        },
        async updateCartItem(item) {
            try {
                const response = await axiosInstance.put(`/cart/${item.cartId}`, {
                    quantity: item.quantity
                });
                if (response.data.code === 1) {
                    ElMessage.success('更新成功');
                }
            } catch (error) {
                ElMessage.error('更新失败');
                this.loadCart();
            }
        },
        async deleteCartItem(item) {
            try {
                const response = await axiosInstance.delete(`/cart/${item.cartId}`);
                if (response.data.code === 1) {
                    ElMessage.success('删除成功');
                    this.loadCart();
                    this.$root.loadCartCount();
                }
            } catch (error) {
                ElMessage.error('删除失败');
            }
        },
        checkout() {
            if (this.cartItems.length === 0) {
                ElMessage.warning('请选择要购买的商品');
                return;
            }
            this.$router.push('/checkout');
        }
    }
};

// 订单确认页组件
const Checkout = {
    template: `
        <div>
            <el-card>
                <template #header>
                    <span>订单确认</span>
                </template>
                <el-steps :active="currentStep" style="margin-bottom: 30px;">
                    <el-step title="选择地址"></el-step>
                    <el-step title="确认订单"></el-step>
                    <el-step title="支付"></el-step>
                </el-steps>
                
                <div v-if="currentStep === 0">
                    <h3>收货地址</h3>
                    <el-radio-group v-model="selectedAddressId" style="width: 100%; margin-top: 20px;">
                        <el-radio v-for="addr in addresses" :key="addr.addressId" :label="addr.addressId" style="display: block; margin-bottom: 10px;">
                            <div style="padding: 10px; border: 1px solid #e4e7ed; border-radius: 4px;">
                                <div>{{ addr.receiverName }} {{ addr.receiverPhone }}</div>
                                <div style="color: #909399; margin-top: 5px;">
                                    {{ addr.province }} {{ addr.city }} {{ addr.district }} {{ addr.detailAddress }}
                                </div>
                            </div>
                        </el-radio>
                    </el-radio-group>
                    <el-button type="text" @click="showAddressDialog = true" style="margin-top: 10px;">+ 添加新地址</el-button>
                    <el-button type="primary" @click="currentStep = 1" :disabled="!selectedAddressId" style="margin-top: 20px;">下一步</el-button>
                </div>
                
                <div v-if="currentStep === 1">
                    <h3>订单信息</h3>
                    <el-table :data="orderItems" style="margin-top: 20px;">
                        <el-table-column label="商品" prop="productName"></el-table-column>
                        <el-table-column label="单价" prop="price"></el-table-column>
                        <el-table-column label="数量" prop="quantity"></el-table-column>
                        <el-table-column label="小计">
                            <template #default="{ row }">¥{{ (row.price * row.quantity).toFixed(2) }}</template>
                        </el-table-column>
                    </el-table>
                    <div style="margin-top: 20px; text-align: right;">
                        <div style="font-size: 20px;">
                            总计: <span style="color: #f56c6c;">¥{{ totalAmount.toFixed(2) }}</span>
                        </div>
                    </div>
                    <el-input v-model="remark" placeholder="订单备注（选填）" style="margin-top: 20px;"></el-input>
                    <div style="margin-top: 20px;">
                        <el-button @click="currentStep = 0">上一步</el-button>
                        <el-button type="primary" @click="createOrder">提交订单</el-button>
                    </div>
                </div>
                
                <div v-if="currentStep === 2">
                    <h3>支付订单</h3>
                    <div style="text-align: center; padding: 40px;">
                        <div style="font-size: 24px; margin-bottom: 20px;">订单号: {{ orderId }}</div>
                        <div style="font-size: 20px; color: #f56c6c; margin-bottom: 30px;">支付金额: ¥{{ totalAmount.toFixed(2) }}</div>
                        <el-radio-group v-model="paymentMethod" style="margin-bottom: 30px;">
                            <el-radio label="alipay">支付宝</el-radio>
                            <el-radio label="wechat">微信支付</el-radio>
                        </el-radio-group>
                        <div>
                            <el-button @click="cancelOrder">取消订单</el-button>
                            <el-button type="primary" @click="payOrder">立即支付</el-button>
                        </div>
                    </div>
                </div>
            </el-card>
            
            <!-- 添加地址对话框 -->
            <el-dialog v-model="showAddressDialog" title="添加收货地址" width="500px">
                <el-form :model="newAddress" label-width="100px">
                    <el-form-item label="收货人">
                        <el-input v-model="newAddress.receiverName"></el-input>
                    </el-form-item>
                    <el-form-item label="手机号">
                        <el-input v-model="newAddress.receiverPhone"></el-input>
                    </el-form-item>
                    <el-form-item label="省份">
                        <el-input v-model="newAddress.province"></el-input>
                    </el-form-item>
                    <el-form-item label="城市">
                        <el-input v-model="newAddress.city"></el-input>
                    </el-form-item>
                    <el-form-item label="区县">
                        <el-input v-model="newAddress.district"></el-input>
                    </el-form-item>
                    <el-form-item label="详细地址">
                        <el-input v-model="newAddress.detailAddress" type="textarea"></el-input>
                    </el-form-item>
                </el-form>
                <template #footer>
                    <el-button @click="showAddressDialog = false">取消</el-button>
                    <el-button type="primary" @click="addAddress">确定</el-button>
                </template>
            </el-dialog>
        </div>
    `,
    data() {
        return {
            currentStep: 0,
            addresses: [],
            selectedAddressId: null,
            orderItems: [],
            totalAmount: 0,
            remark: '',
            orderId: null,
            paymentMethod: 'alipay',
            showAddressDialog: false,
            newAddress: {
                receiverName: '',
                receiverPhone: '',
                province: '',
                city: '',
                district: '',
                detailAddress: ''
            }
        };
    },
    mounted() {
        this.loadAddresses();
        this.loadCartItems();
    },
    methods: {
        async loadAddresses() {
            try {
                const response = await axiosInstance.get('/addresses');
                if (response.data.code === 1) {
                    this.addresses = response.data.data || [];
                    if (this.addresses.length > 0) {
                        this.selectedAddressId = this.addresses[0].addressId;
                    }
                }
            } catch (error) {
                console.error('加载地址失败:', error);
            }
        },
        async loadCartItems() {
            try {
                const response = await axiosInstance.get('/cart');
                if (response.data.code === 1) {
                    const items = response.data.data || [];
                    this.orderItems = items.map(item => ({
                        productId: item.productId,
                        productName: item.productName,
                        price: item.price,
                        quantity: item.quantity
                    }));
                    this.totalAmount = items.reduce((sum, item) => sum + item.price * item.quantity, 0);
                }
            } catch (error) {
                console.error('加载购物车失败:', error);
            }
        },
        async addAddress() {
            try {
                const response = await axiosInstance.post('/addresses', this.newAddress);
                if (response.data.code === 1) {
                    ElMessage.success('添加成功');
                    this.showAddressDialog = false;
                    this.loadAddresses();
                    this.newAddress = {
                        receiverName: '',
                        receiverPhone: '',
                        province: '',
                        city: '',
                        district: '',
                        detailAddress: ''
                    };
                }
            } catch (error) {
                ElMessage.error('添加失败');
            }
        },
        async createOrder() {
            try {
                const response = await axiosInstance.post('/orders', {
                    addressId: this.selectedAddressId,
                    items: this.orderItems,
                    remark: this.remark
                });
                if (response.data.code === 1) {
                    this.orderId = response.data.data.orderId;
                    this.totalAmount = response.data.data.totalAmount;
                    this.currentStep = 2;
                }
            } catch (error) {
                ElMessage.error('创建订单失败');
            }
        },
        async payOrder() {
            try {
                const response = await axiosInstance.post('/payment/pay', {
                    orderId: this.orderId,
                    paymentMethod: this.paymentMethod
                });
                if (response.data.code === 1) {
                    ElMessage.success('支付成功');
                    this.$router.push('/orders');
                }
            } catch (error) {
                ElMessage.error('支付失败');
            }
        },
        async cancelOrder() {
            try {
                const response = await axiosInstance.put(`/orders/${this.orderId}/cancel`);
                if (response.data.code === 1) {
                    ElMessage.success('订单已取消');
                    this.$router.push('/orders');
                }
            } catch (error) {
                ElMessage.error('取消订单失败');
            }
        }
    }
};

// 订单列表页组件
const Orders = {
    template: `
        <div>
            <el-card>
                <template #header>
                    <div style="display: flex; justify-content: space-between;">
                        <span>我的订单</span>
                        <el-select v-model="statusFilter" placeholder="筛选状态" style="width: 150px;" @change="loadOrders">
                            <el-option label="全部" :value="null"></el-option>
                            <el-option label="待支付" :value="1"></el-option>
                            <el-option label="已支付" :value="2"></el-option>
                            <el-option label="已发货" :value="3"></el-option>
                            <el-option label="已完成" :value="4"></el-option>
                            <el-option label="已取消" :value="5"></el-option>
                        </el-select>
                    </div>
                </template>
                <div v-if="orders.length === 0" style="text-align: center; padding: 40px; color: #909399;">
                    暂无订单
                </div>
                <div v-else>
                    <el-card v-for="order in orders" :key="order.orderId" style="margin-bottom: 20px;">
                        <div style="display: flex; justify-content: space-between; margin-bottom: 15px;">
                            <div>
                                <span>订单号: {{ order.orderId }}</span>
                                <el-tag :type="getStatusType(order.status)" style="margin-left: 10px;">
                                    {{ getStatusText(order.status) }}
                                </el-tag>
                            </div>
                            <div style="color: #909399;">{{ formatDate(order.createdAt) }}</div>
                        </div>
                        <el-table :data="order.items" style="margin-bottom: 15px;">
                            <el-table-column label="商品" prop="productName"></el-table-column>
                            <el-table-column label="单价" prop="price"></el-table-column>
                            <el-table-column label="数量" prop="quantity"></el-table-column>
                            <el-table-column label="小计">
                                <template #default="{ row }">¥{{ row.subtotal.toFixed(2) }}</template>
                            </el-table-column>
                        </el-table>
                        <div style="text-align: right; margin-bottom: 15px;">
                            <span style="font-size: 18px;">总计: </span>
                            <span style="font-size: 20px; color: #f56c6c; font-weight: bold;">¥{{ order.totalAmount.toFixed(2) }}</span>
                        </div>
                        <div style="text-align: right;">
                            <el-button v-if="order.status === 1" type="primary" @click="payOrder(order.orderId)">支付</el-button>
                            <el-button v-if="order.status === 1" @click="cancelOrder(order.orderId)">取消订单</el-button>
                            <el-button v-if="order.status === 3" type="success" @click="confirmReceipt(order.orderId)">确认收货</el-button>
                        </div>
                    </el-card>
                </div>
            </el-card>
        </div>
    `,
    data() {
        return {
            orders: [],
            statusFilter: null
        };
    },
    mounted() {
        this.loadOrders();
    },
    methods: {
        async loadOrders() {
            try {
                const params = this.statusFilter ? { status: this.statusFilter } : {};
                const response = await axiosInstance.get('/orders', { params });
                if (response.data.code === 1) {
                    this.orders = response.data.data || [];
                }
            } catch (error) {
                console.error('加载订单失败:', error);
            }
        },
        async payOrder(orderId) {
            this.$router.push(`/checkout?orderId=${orderId}`);
        },
        async cancelOrder(orderId) {
            try {
                const response = await axiosInstance.put(`/orders/${orderId}/cancel`);
                if (response.data.code === 1) {
                    ElMessage.success('订单已取消');
                    this.loadOrders();
                }
            } catch (error) {
                ElMessage.error('取消订单失败');
            }
        },
        async confirmReceipt(orderId) {
            try {
                const response = await axiosInstance.put(`/orders/${orderId}/confirm`);
                if (response.data.code === 1) {
                    ElMessage.success('确认收货成功');
                    this.loadOrders();
                }
            } catch (error) {
                ElMessage.error('确认收货失败');
            }
        },
        getStatusText(status) {
            const statusMap = {
                1: '待支付',
                2: '已支付',
                3: '已发货',
                4: '已完成',
                5: '已取消'
            };
            return statusMap[status] || '未知';
        },
        getStatusType(status) {
            const typeMap = {
                1: 'warning',
                2: 'info',
                3: 'success',
                4: '',
                5: 'danger'
            };
            return typeMap[status] || '';
        },
        formatDate(dateStr) {
            if (!dateStr) return '';
            return new Date(dateStr).toLocaleString('zh-CN');
        }
    }
};

// 路由配置
const routes = [
    { path: '/', component: Home },
    { path: '/product/:id', component: ProductDetail },
    { path: '/cart', component: Cart },
    { path: '/checkout', component: Checkout },
    { path: '/orders', component: Orders }
];

const router = createRouter({
    history: createWebHashHistory(),
    routes
});

// 主应用
const App = {
    data() {
        return {
            userInfo: null,
            cartCount: 0,
            showLogin: false,
            showRegister: false,
            searchKeyword: '',
            loginForm: {
                account: '',
                password: ''
            },
            registerForm: {
                username: '',
                email: '',
                password: '',
                role: 1
            }
        };
    },
    mounted() {
        this.checkLogin();
        this.loadCartCount();
    },
    methods: {
        checkLogin() {
            const token = localStorage.getItem('token');
            const userInfo = localStorage.getItem('userInfo');
            if (token && userInfo) {
                this.userInfo = JSON.parse(userInfo);
            }
        },
        async handleLogin() {
            try {
                const response = await axiosInstance.post('/login', this.loginForm);
                if (response.data.code === 1) {
                    const data = response.data.data;
                    localStorage.setItem('token', data.token);
                    localStorage.setItem('userInfo', JSON.stringify({
                        userId: data.userId,
                        username: data.username,
                        role: data.role,
                        avatar: data.avatar
                    }));
                    this.userInfo = {
                        userId: data.userId,
                        username: data.username,
                        role: data.role,
                        avatar: data.avatar
                    };
                    this.showLogin = false;
                    ElMessage.success('登录成功');
                    this.loadCartCount();
                }
            } catch (error) {
                ElMessage.error('登录失败');
            }
        },
        async handleRegister() {
            try {
                const response = await axiosInstance.post('/register', this.registerForm);
                if (response.data.code === 1) {
                    ElMessage.success('注册成功，请登录');
                    this.showRegister = false;
                    this.showLogin = true;
                }
            } catch (error) {
                ElMessage.error('注册失败');
            }
        },
        handleUserCommand(command) {
            if (command === 'logout') {
                localStorage.removeItem('token');
                localStorage.removeItem('userInfo');
                this.userInfo = null;
                this.cartCount = 0;
                ElMessage.success('已退出登录');
                this.$router.push('/');
            } else if (command === 'orders') {
                this.$router.push('/orders');
            } else if (command === 'admin') {
                window.open('#/admin', '_blank');
            }
        },
        async loadCartCount() {
            if (!localStorage.getItem('token')) {
                this.cartCount = 0;
                return;
            }
            try {
                const response = await axiosInstance.get('/cart');
                if (response.data.code === 1) {
                    this.cartCount = (response.data.data || []).length;
                }
            } catch (error) {
                this.cartCount = 0;
            }
        },
        goHome() {
            this.$router.push('/');
        },
        goToCart() {
            if (!localStorage.getItem('token')) {
                this.showLogin = true;
                return;
            }
            this.$router.push('/cart');
        },
        searchProducts() {
            if (this.searchKeyword.trim()) {
                this.$router.push(`/?search=${encodeURIComponent(this.searchKeyword)}`);
            }
        }
    }
};

// 创建应用实例
const app = createApp(App);
app.use(router);
app.mount('#app');

