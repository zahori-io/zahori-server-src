const PROXY_CONFIG = [
    {
        context: [
            "/zahori/api",
            "/zahori/login",
            "/zahori/account",
            "/zahori/languages",
            "/zahori/assets"
        ],
        target: "http://localhost:9090",
        secure: false
    }
]

module.exports = PROXY_CONFIG;
