import org.assertj.core.api.Assertions.assertThat
import org.example.GitInfo
import org.example.GitSettings
import org.example.UnmergeBranch
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import util.ApplicationPath
import util.SetupEnvironment

class GitInfoTest {

    private val applicationPath = ApplicationPath()

    @BeforeEach
    fun setup() {
        SetupEnvironment.SETUP_TEST1.execute()

    }

    @Test
    fun identifyJiraTicketsThatNeedsToBeMergeToMainFromDevelop() {
        // given
        val settings = GitSettings(applicationPath.gitTest1(), "main", "develop")

        // when
        val gitInfo = GitInfo(settings)
        val workingTickets = gitInfo.findWorkingTickets()

        // then
        assertThat(workingTickets).containsExactlyInAnyOrder("ABC-4", "ABC-1", "ABC-2", "FIX-123")
    }

    @Test
    fun identifyJiraTicketsThatNeedsToBeMergeToDevelopFromFeature_ABC_3() {
        // given
        val settings = GitSettings(applicationPath.gitTest1(), "develop", "feature/ABC-3")

        // when
        val gitInfo = GitInfo(settings)
        val workingTickets = gitInfo.findWorkingTickets()

        // then
        assertThat(workingTickets).containsExactlyInAnyOrder("ABC-3")
    }

    @Test
    fun unmergeBranches() {
        // given
        val settings = GitSettings(applicationPath.gitTest1(), "main", "develop")

        // when
        val gitInfo = GitInfo(settings)
        val unmergeBranches = gitInfo.findUnmergeBranches()

        // then
        assertThat(unmergeBranches).containsExactlyInAnyOrder(
            UnmergeBranch("main", "release/r1"),
            UnmergeBranch("develop", "release/r1"),
        )
    }

    @Test
    fun findUnmergeBranchesAndMergeIt() {
        // given
        val settings = GitSettings(applicationPath.gitTest1(), "main", "develop")

        // when
        val gitInfo = GitInfo(settings)
        val unmergeBranches = gitInfo.findUnmergeBranches()
        gitInfo.mergeBranches(unmergeBranches)

        // then
        assertThat(gitInfo.findUnmergeBranches()).hasSize(0)
    }
}