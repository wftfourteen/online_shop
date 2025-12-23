const { createApp } = Vue;
const { ElButton, ElCard, ElForm, ElFormItem, ElInput, ElRadioGroup, ElRadioButton, ElMessage } = ElementPlus;
const { User, Lock } = ElementPlusIconsVue;

createApp({
    components: {
        ElButton,
        ElCard,
        ElForm,
        ElFormItem,
        ElInput,
        ElRadioGroup,
        ElRadioButton,
        ElIcon: ElementPlus.ElIcon,
        User,
        Lock
    },
    data() {
        return {
            loading: false,
            loginForm: {
                account: '',
                password: '',
                role: 1 // 1-顾客, 2-商家
            },
            rules: {
                account: [
                    { required: true, message: '请输入用户名或邮箱', trigger: 'blur' }
                ],
                password: [
                    { required: true, message: '请输入密码', trigger: 'blur' },
                    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
                ],
                role: [
                    { required: true, message: '请选择身份', trigger: 'change' }
                ]
            }
        };
    },
    methods: {
        async handleLogin() {
            try {
                await this.$refs.loginFormRef.validate();
                this.loading = true;
                
                const res = await API.login(this.loginForm.account, this.loginForm.password);
                
                if (res.code === 1 && res.data) {
                    // 保存用户信息和Token
                    localStorage.setItem('token', res.data.token);
                    localStorage.setItem('userInfo', JSON.stringify(res.data));
                    
                    // 检查角色是否匹配
                    if (res.data.role !== this.loginForm.role) {
                        ElMessage.error('身份选择不正确，请重新选择');
                        this.loading = false;
                        return;
                    }
                    
                    ElMessage.success('登录成功');
                    
                    // 根据角色跳转
                    if (res.data.role === 2) {
                        // 商家
                        window.location.href = './merchant.html';
                    } else {
                        // 顾客
                        window.location.href = './customer.html';
                    }
                } else {
                    ElMessage.error(res.msg || '登录失败');
                    this.loading = false;
                }
            } catch (error) {
                this.loading = false;
                console.error('登录错误:', error);
            }
        }
    }
}).use(ElementPlus).mount('#app');
