package headers
// Test: Valid single header
import (
	"testing"
	"github.com/stretchr/testify/require"
	"github.com/stretchr/testify/assert"
)

func TestHeaders(t *testing.T){
	h := NewHeaders()
	data := []byte("Host: localhost:42069\r\nFoo: bar\r\n\r\n")
	n, done, err := h.Parse(data)
	require.NoError(t, err)
	require.NotNil(t, h.headers)
	assert.Equal(t, "localhost:42069", h.Get("Host"))
	assert.Equal(t, "bar", h.Get("foo"))
	assert.Equal(t, "", h.Get("MissingKey"))
	assert.Equal(t, 35, n)
	assert.True(t, done)

	// Test: Invalid spacing header
	h = NewHeaders()
	data = []byte("       Host : localhost:42069       \r\n\r\n")
	n, done, err = h.Parse(data)
	require.Error(t, err)
	assert.Equal(t, 0, n)
	assert.False(t, done)

	//Test: Case Insensivity
	h = NewHeaders()
	data = []byte("HoSt: localhost:42069\r\n\r\n")
	_, _, err = h.Parse(data);
	require.NoError(t, err);
	require.NotNil(t, h.headers);
	assert.Equal(t, "localhost:42069", h.Get("Host"))

	//Test: Invalid Token Present
	h = NewHeaders()
	data = []byte("H©st: localhost:42069\r\n\r\n")
	n, done, err = h.Parse(data)
	require.Error(t, err)
	assert.Equal(t, 0, n)
	assert.False(t, done)
}
