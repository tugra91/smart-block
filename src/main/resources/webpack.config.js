module.exports = {
  entry: [
    './static/src/index.js'
  ],
  module: {
    rules: [
      {
        test: /\.(js|jsx)$/,
        exclude: /node_modules/,
        use: ['babel-loader']
      },
      {
        test: /\.(css)$/,
        use: ['style-loader','css-loader']
      }
    ]
  },
  resolve: {
    extensions: ['*', '.js', '.jsx','.css']
  },
  output: {
    path: __dirname + '/static/src',
    publicPath: '/',
    filename: 'bundle.js'
  }
};
