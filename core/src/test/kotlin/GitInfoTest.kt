import org.assertj.core.api.Assertions.assertThat
import org.example.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import util.ApplicationPath
import util.SetupEnvironment
import java.io.File
import java.io.PrintWriter
import java.nio.file.Path

class GitInfoTest {

    private val applicationPath = ApplicationPath()

    @BeforeEach
    fun setup() {
        SetupEnvironment.SETUP_TEST1.execute()

    }

    @Test
    fun identifyJiraTicketsThatNeedsToBeMergeToMainFromDevelop() {
        // given
        val settings = GitSettings(applicationPath.gitTest1(), Branch("main"),  Branch("develop"))

        // when
        val gitInfo = GitInfo(settings)
        val workingTickets = gitInfo.findWorkingTickets()

        // then
        assertThat(workingTickets).containsExactlyInAnyOrder(
            Ticket("ABC-4"),
            Ticket("ABC-1"),
            Ticket("ABC-2"),
            Ticket("FIX-123"))

    }

    @Test
    fun detectUncommitedChanges() {
        // given
        val settings = GitSettings(applicationPath.gitTest1(), Branch("main"),  Branch("develop"))

        // when
        val gitInfo = GitInfo(settings)
        assertThat(gitInfo.hasUncommitedChanges()).isFalse()

        // when create file
        createFile()
        assertThat(gitInfo.hasUncommitedChanges()).isTrue()
    }

    private fun createFile() {
        val file = File(Path.of(applicationPath.gitTest1().toString(), "test.txt").toString())
        PrintWriter(file.writer()).use { writer ->
            writer.println("Hello, this is the content of the file!")
        }
    }

    @Test
    fun identifyJiraTicketsThatNeedsToBeMergeToDevelopFromFeature_ABC_3() {
        // given
        val settings = GitSettings(applicationPath.gitTest1(), Branch("develop"), Branch("feature/ABC-3"))

        // when
        val gitInfo = GitInfo(settings)
        val workingTickets = gitInfo.findWorkingTickets()

        // then
        assertThat(workingTickets).containsExactlyInAnyOrder(Ticket("ABC-3"))
    }

    @Test
    fun unmergeBranches() {
        // given
        val settings = GitSettings(applicationPath.gitTest1(), Branch("main"), Branch("develop"))

        // when
        val gitInfo = GitInfo(settings)
        val unmergeBranches = gitInfo.findUnmergeBranches()

        // then
        assertThat(unmergeBranches).containsExactlyInAnyOrder(
            UnmergeBranch(Branch("main"), Branch("release/r1")),
            UnmergeBranch(Branch("develop"), Branch("release/r1")),
        )
    }

    @Test
    fun findUnmergeBranchesAndMergeIt() {
        // given
        val settings = GitSettings(applicationPath.gitTest1(), Branch("main"), Branch("develop"))

        // when
        val gitInfo = GitInfo(settings)
        val unmergeBranches = gitInfo.findUnmergeBranches()
        gitInfo.mergeBranches(unmergeBranches)

        // then
        assertThat(gitInfo.findUnmergeBranches()).hasSize(0)
    }
}