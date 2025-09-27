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
	host, _ := h.Get("Host")
	assert.Equal(t, "localhost:42069", host)
	foo, _ := h.Get("foo");
	assert.Equal(t, "bar", foo)
	missing_key, _ := h.Get("MissingKey");
	assert.Equal(t, "", missing_key)
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
	host, _ = h.Get("Host");
	assert.Equal(t, "localhost:42069", host)

	//Test: Invalid Token Present
	h = NewHeaders()
	data = []byte("H©st: localhost:42069\r\n\r\n")
	n, done, err = h.Parse(data)
	require.Error(t, err)
	assert.Equal(t, 0, n)
	assert.False(t, done)

	//Test: Invalid Token Present
	h = NewHeaders()
	data = []byte("Host: localhost:42069\r\nHost: localhost:42068\r\n\r\n")
	n, done, err = h.Parse(data)
	require.NotNil(t, h.headers)
	host, _ = h.Get("Host");
	assert.Equal(t, "localhost:42069,localhost:42068", host)
	assert.True(t, done)
}
