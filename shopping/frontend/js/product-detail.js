const { createApp } = Vue;
const { ElContainer, ElHeader, ElMain, ElButton, ElIcon, ElInputNumber, ElTag, ElMessage } = ElementPlus;
const { ShoppingCart, ShoppingBag } = ElementPlusIconsVue;

createApp({
    components: {
        ElContainer,
        ElHeader,
        ElMain,
        ElButton,
        ElIcon,
        ElInputNumber,
        ElTag,
        ShoppingCart,
        ShoppingBag
    },
    data() {
        return {
            loading: false,
            product: null,
            quantity: 1,
            currentImageIndex: 0,
            showImagePreview: false,
            cartCount: 0
        };
    },
    computed: {
        currentImage() {
            if (this.product && this.product.images && this.product.images.length > 0) {
                const img = this.product.images[this.currentImageIndex] || this.product.images[0];
                return this.getProductImageUrl(img);
            }
            return '/default-product.png';
        }
    },
    mounted() {
        const productId = new URLSearchParams(window.location.search).get('id');
        if (productId) {
            this.loadProductDetail(productId);
            this.loadCartCount();
        } else {
            ElMessage.error('商品ID无效');
            setTimeout(() => {
                window.location.href = './customer.html';
            }, 1500);
        }
    },
    methods: {
        async loadProductDetail(productId) {
            this.loading = true;
            try {
                const res = await API.getProductDetail(parseInt(productId));
                if (res.code === 1 && res.data) {
                    this.product = res.data;
                    if (this.product.stock === 0) {
                        this.quantity = 0;
                    }
                } else {
                    ElMessage.error('商品不存在');
                    setTimeout(() => {
                        window.location.href = './customer.html';
                    }, 1500);
                }
            } catch (error) {
                ElMessage.error('加载商品详情失败');
            } finally {
                this.loading = false;
            }
        },
        
        async loadCartCount() {
            try {
                const res = await API.getCartList();
                if (res.code === 1 && res.data) {
                    const items = res.data || [];
                    this.cartCount = items.reduce((sum, item) => sum + (item.quantity || 0), 0);
                }
            } catch (error) {
                // 忽略错误
            }
        },
        
        async addToCart() {
            if (!localStorage.getItem('token')) {
                ElMessage.warning('请先登录');
                window.location.href = './login.html';
                return;
            }
            
            if (this.quantity > this.product.stock) {
                ElMessage.error(`库存不足，最多可购买 ${this.product.stock} 件`);
                this.quantity = this.product.stock;
                return;
            }
            
            try {
                const res = await API.addToCart(this.product.productId, this.quantity);
                if (res.code === 1) {
                    ElMessage.success('已加入购物车');
                    this.loadCartCount();
                }
            } catch (error) {
                console.error('加入购物车失败:', error);
            }
        },
        
        async buyNow() {
            if (!localStorage.getItem('token')) {
                ElMessage.warning('请先登录');
                window.location.href = './login.html';
                return;
            }
            
            if (this.quantity > this.product.stock) {
                ElMessage.error(`库存不足，最多可购买 ${this.product.stock} 件`);
                this.quantity = this.product.stock;
                return;
            }
            
            if (this.product.stock === 0) {
                ElMessage.error('商品已售罄');
                return;
            }
            
            // 跳转到订单确认页，传递商品信息
            const orderData = {
                items: [{
                    productId: this.product.productId,
                    quantity: this.quantity
                }]
            };
            sessionStorage.setItem('quickOrder', JSON.stringify(orderData));
            window.location.href = './checkout.html';
        },
        
        goToCart() {
            window.location.href = './cart.html';
        },
        
        getProductImageUrl(imageUrl) {
            if (!imageUrl) return '/default-product.png';
            if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) return imageUrl;
            if (imageUrl.includes('oss') || imageUrl.includes('aliyuncs')) return imageUrl;
            return API_CONFIG.BASE_URL + imageUrl;
        },
        
        getImageList() {
            if (!this.product || !this.product.images || this.product.images.length === 0) {
                return ['/default-product.png'];
            }
            return this.product.images.map(img => this.getProductImageUrl(img));
        }
    }
}).use(ElementPlus).mount('#app');
