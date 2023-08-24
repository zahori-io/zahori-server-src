const PROXY_CONFIG = [
    {
        context: [
            "/zahori/api",
            "/zahori/login",
            "/zahori/account"
        ],
        target: "http://localhost:9090",
        secure: false
    }
]

module.exports = PROXY_CONFIG;
