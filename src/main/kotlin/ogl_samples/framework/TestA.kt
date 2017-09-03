package ogl_samples.framework

import gli.wasInit
import glm_.BYTES
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL20.glDeleteProgram
import org.lwjgl.opengl.GL20.glIsProgram
import org.lwjgl.opengl.GL30.glDeleteVertexArrays
import org.lwjgl.opengl.GL30.glIsVertexArray
import uno.buffer.*
import uno.caps.Caps
import uno.glf.VertexLayout
import uno.glf.glf
import uno.gln.checkError
import uno.gln.initVertexArray
import uno.kotlin.buffers.filter
import java.nio.FloatBuffer
import java.nio.ShortBuffer

abstract class TestA(title: String, profile: Caps.Profile, major: Int, minor: Int) : Test(title, profile, major, minor) {

    var elementCount = 0
    var elementSize = 0
    lateinit var elementData: ShortBuffer

    var vertexCount = 0
    var positionSize = 0
    lateinit var positionData: FloatBuffer

    object Buffer {
        val VERTEX = 0
        val ELEMENT = 1
        val MAX = 2
    }

    val bufferName = intBufferBig(Buffer.MAX).also { glGenBuffers(it) }
    var programName = 0
    var uniformMVP = 0
    var uniformDiffuse = 0
    val vertexArrayName = intBufferBig(1)

    override fun begin(): Boolean {

        var validated = true

        if (validated)
            validated = initProgram()

        if (validated)
            validated = initBuffer()

        if(validated)
            validated = initVertexArray()

        return validated
    }

    open fun initProgram() = true

    open fun initBuffer() = true

    open fun initBuffers(vertices: FloatArray, elements: ShortArray): Boolean {

        initArrayBuffer(*vertices)

        initElementeBuffer(*elements)

        return checkError("TestB.initBuffers")
    }

    open fun initArrayBuffer(vararg args: Float) {

        positionData = floatBufferOf(*args)

        vertexCount = args.size
        positionSize = vertexCount * Float.BYTES

        glBindBuffer(GL_ARRAY_BUFFER, bufferName[Buffer.VERTEX])
        glBufferData(GL_ARRAY_BUFFER, positionData, GL_STATIC_DRAW)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    open fun initElementeBuffer(vararg args: Short) {

        elementData = shortBufferOf(*args)

        elementCount = args.size
        elementSize = elementCount * Short.BYTES

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferName[Buffer.ELEMENT])
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementData, GL_STATIC_DRAW)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    open fun initVertexArray() = true

    open fun initVertexArray(vertexLayout: VertexLayout): Boolean {

        when(vertexLayout) {

            glf.pos2 -> {

                initVertexArray(vertexArrayName) {
                    array(bufferName[Buffer.VERTEX], glf.pos2)
                    element(bufferName[Buffer.ELEMENT])
                }
            }
        }
        return checkError("TestB.initVertexArray")
    }

    override abstract fun render(): Boolean

    override fun end(): Boolean {

        if (glIsProgram(programName)) glDeleteProgram(programName)
        bufferName.filter(GL20::glIsProgram).map(GL20::glDeleteProgram)

        if (wasInit { positionData }) positionData.destroy()
        if (wasInit { elementData }) elementData.destroy()

        if(glIsVertexArray(vertexArrayName[0])) glDeleteVertexArrays(vertexArrayName)

        destroyBuf(bufferName, vertexArrayName)

        return checkError("TestB.initVertexArray")
    }
}