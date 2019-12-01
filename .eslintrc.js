module.exports = {
  'extends': 'airbnb',
  'parser': 'babel-eslint',
  'env': {
    'jest': true,
  },
  'rules': {
    'no-use-before-define': 'off',
    'react/jsx-filename-extension': 'off',
    'react/prop-types': 'off',
    'react/destructuring-assignment': 'off',
    'comma-dangle': 'off'
  },
  'globals': {
    "fetch": false
  }
}
