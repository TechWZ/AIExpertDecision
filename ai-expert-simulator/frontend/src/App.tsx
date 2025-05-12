import { useState, useEffect, useCallback } from 'react'
import './App.css' // Keep default styling for now

// Define types for better code clarity
interface AvailableModel {
  id: string;
  name: string;
  provider: string;
}

interface RoleRecommendation {
  role_name: string;
  score: number | null;
}

interface RolePrompt {
  [roleName: string]: string;
}

interface SelectedRoles {
  [roleName: string]: boolean;
}

interface SelectedRoleModels {
  [roleName: string]: string;
}

interface SimulationResults {
  [roleName: string]: string;
}

// --- 默认提示词 (中文) ---
const defaultPrompt = "请基于所提供的研究内容，以你扮演的专家角色，提出关键见解、潜在挑战和具体建议。";

function App() {
  // --- 状态变量 ---
  const [currentStep, setCurrentStep] = useState<number>(1); // 当前步骤

  // 步骤 1
  const [researchContent, setResearchContent] = useState<string>('');
  const [messageRecommend, setMessageRecommend] = useState<string>(''); // 推荐步骤的消息
  const [isLoadingRecommend, setIsLoadingRecommend] = useState<boolean>(false);

  // 步骤 3 (合并了角色和模型选择)
  const [recommendedRoles, setRecommendedRoles] = useState<RoleRecommendation[]>([]);
  const [selectedRoles, setSelectedRoles] = useState<SelectedRoles>({});
  const [selectedRoleModels, setSelectedRoleModels] = useState<SelectedRoleModels>({});
  const [availableModels, setAvailableModels] = useState<AvailableModel[]>([]);
  // New state for custom role input
  const [customRoleName, setCustomRoleName] = useState<string>('');
  const [showCustomInput, setShowCustomInput] = useState<boolean>(false);

  // 步骤 4 (原步骤 5 - 定义提示词)
  const [rolePrompts, setRolePrompts] = useState<RolePrompt>({});

  // 步骤 5/6 (原步骤 6/7 - 运行和结果)
  const [simulationResults, setSimulationResults] = useState<SimulationResults>({});
  const [isLoadingSimulation, setIsLoadingSimulation] = useState<boolean>(false);
  const [simulationMessage, setSimulationMessage] = useState<string>('');

  // --- 清理函数 ---
  const resetToStep1 = useCallback(() => {
    setCurrentStep(1);
    setMessageRecommend('');
    setRecommendedRoles([]);
    setSelectedRoles({});
    setSelectedRoleModels({});
    setRolePrompts({});
    setSimulationResults({});
    setSimulationMessage('');
    setCustomRoleName('');
    setShowCustomInput(false);
  }, []); // useCallback 依赖为空，函数引用不变

  // --- Effects ---
  // 获取可用模型 (仅一次)
  useEffect(() => {
    const fetchModels = async () => {
      setMessageRecommend("正在加载可用模型..."); // Indicate loading
      try {
        const response = await fetch('http://localhost:5001/api/available_models');
        if (!response.ok) throw new Error('无法获取模型列表');
        const models: AvailableModel[] = await response.json();
        setAvailableModels(models);
        setMessageRecommend(""); // Clear loading message
      } catch (error) {
        console.error("获取模型时出错:", error);
        setMessageRecommend("错误: 无法从后端获取可用模型列表。");
      }
    };
    fetchModels();
  }, []); // 空依赖数组确保仅在挂载时运行

  // --- Handlers ---

  // 步骤 1: 请求角色推荐
  const handleRecommendRoles = async () => {
    if (!researchContent.trim()) {
      setMessageRecommend('请输入研究内容。');
      return;
    }
    setIsLoadingRecommend(true);
    setMessageRecommend('');
    setRecommendedRoles([]);
    setSelectedRoles({});
    setSelectedRoleModels({});
    setRolePrompts({});
    setSimulationResults({});
    setSimulationMessage('');

    try {
      const apiUrl = 'http://localhost:5001/api/submit_content';
      const response = await fetch(apiUrl, {
          method: 'POST',
          headers: {'Content-Type': 'application/json'},
          body: JSON.stringify({ content: researchContent }),
      });
      const result = await response.json();
      if (response.ok && result.roles && Array.isArray(result.roles)) {
        // Ensure score is number or null
        const validatedRoles = result.roles.map((r: any) => ({
            role_name: r.role_name,
            score: typeof r.score === 'number' ? r.score : null 
        }));
        setRecommendedRoles(validatedRoles); 

        const initialPrompts: RolePrompt = {};
        const initialModels: SelectedRoleModels = {};
        const defaultModelId = availableModels.length > 0 ? availableModels[0].id : '';

        validatedRoles.forEach((roleData: RoleRecommendation) => { 
          initialPrompts[roleData.role_name] = defaultPrompt;
          initialModels[roleData.role_name] = defaultModelId;
        });
        setRolePrompts(initialPrompts);
        setSelectedRoleModels(initialModels);
        setMessageRecommend(''); 
        setCurrentStep(3); 
      } else {
        setMessageRecommend(`错误: ${result.message || result.error || '未能获取有效角色推荐。'}`);
        setRecommendedRoles([]);
      }
    } catch (error) {
      console.error('推荐角色时出错:', error);
      setMessageRecommend('错误: 连接后端推荐服务失败。');
      setRecommendedRoles([]);
    } finally {
      setIsLoadingRecommend(false);
    }
  };

  // 步骤 3: 处理角色勾选
  const handleRoleCheckboxChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { name: roleName, checked } = event.target;
    setSelectedRoles(prevSelected => ({ ...prevSelected, [roleName]: checked }));
    // If unchecking, we could optionally clear the model selection for that role
    // if (!checked) {
    //   setSelectedRoleModels(prevModels => {
    //     const newModels = { ...prevModels };
    //     delete newModels[roleName]; // Or set to ''
    //     return newModels;
    //   });
    // }
  };

  // 步骤 3: 处理单个角色的模型选择
  const handleRoleModelChange = (event: React.ChangeEvent<HTMLSelectElement>, roleName: string) => {
    const modelId = event.target.value;
    setSelectedRoleModels(prevModels => ({
      ...prevModels,
      [roleName]: modelId,
    }));
  };

  // 步骤 3 -> 步骤 4: 进入提示词定义
  const goToPromptDefinition = () => {
    const countSelected = Object.values(selectedRoles).filter(Boolean).length;
    if (countSelected === 0) {
        setMessageRecommend('请至少选择一个专家角色。');
        return;
    }
    // Check if all selected roles have a model selected
    const allModelsSelected = Object.entries(selectedRoles).every(([roleName, isSelected]) => 
        !isSelected || (isSelected && selectedRoleModels[roleName] && selectedRoleModels[roleName] !== '')
    );
    if (!allModelsSelected) {
        setMessageRecommend('请为所有选中的角色选择一个模型引擎。');
        return;
    }

    setMessageRecommend('');
    setCurrentStep(4); // Go to Step 4 (was 5) - Prompt Definition
  };

  // 步骤 4 (原 5): 处理提示词修改
  const handlePromptChange = (event: React.ChangeEvent<HTMLTextAreaElement>) => {
    const { name: roleName, value } = event.target;
    setRolePrompts(prevPrompts => ({ ...prevPrompts, [roleName]: value }));
  };

  // 步骤 4 -> 步骤 5/6: 运行模拟
  const handleRunSimulation = async () => {
    const rolesToSimulate = Object.entries(selectedRoles)
                              .filter(([, isSelected]) => isSelected)
                              .map(([roleName]) => ({
                                role_name: roleName,
                                prompt: rolePrompts[roleName] || defaultPrompt,
                                model_id: selectedRoleModels[roleName] // Get model for this role
                              }));

    // Validation (should be guaranteed by button state, but double check)
    if (rolesToSimulate.length === 0 || rolesToSimulate.some(role => !role.model_id)) {
      setSimulationMessage("错误: 角色或模型选择不完整。");
      return;
    }

    setIsLoadingSimulation(true);
    setSimulationMessage('');
    setSimulationResults({});

    try {
      const apiUrl = 'http://localhost:5001/api/run_simulation';
      const payload = {
        content: researchContent,
        selected_roles: rolesToSimulate, // Already contains model_id per role
        // No top-level model_name needed
      };
      const response = await fetch(apiUrl, { 
          method: 'POST',
          headers: {'Content-Type': 'application/json'},
          body: JSON.stringify(payload),
      });
      const result = await response.json();

      if (response.ok && result.results) {
        setSimulationMessage('');
        setSimulationResults(result.results);
        setCurrentStep(6); // Go to Step 6 (was 7) - Results Display
      } else {
        setSimulationMessage(`错误: ${result.message || result.error || '模拟运行失败。'}`);
        setSimulationResults({});
      }
    } catch (error) {
      console.error("模拟运行时出错:", error);
      setSimulationMessage("错误: 连接后端模拟服务失败。");
      setSimulationResults({});
    } finally {
      setIsLoadingSimulation(false);
    }
  };

  // New Handler: Add Custom Role
  const handleAddCustomRole = () => {
    const trimmedName = customRoleName.trim();
    if (!trimmedName) {
        setMessageRecommend("请输入自定义专家名称。");
        return;
    }
    // Check if role already exists (case-insensitive check recommended)
    const exists = recommendedRoles.some(r => r.role_name.toLowerCase() === trimmedName.toLowerCase());
    if (exists) {
        setMessageRecommend(`角色 "${trimmedName}" 已存在。`);
        return;
    }

    const newRole: RoleRecommendation = { role_name: trimmedName, score: null }; // Use null score
    const defaultModelId = availableModels.length > 0 ? availableModels[0].id : '';

    setRecommendedRoles(prevRoles => [...prevRoles, newRole]); // Add to the list
    setSelectedRoles(prevSelected => ({ ...prevSelected, [trimmedName]: true })); // Select it by default
    setSelectedRoleModels(prevModels => ({ ...prevModels, [trimmedName]: defaultModelId })); // Assign default model
    setRolePrompts(prevPrompts => ({ ...prevPrompts, [trimmedName]: defaultPrompt })); // Assign default prompt
    
    setCustomRoleName(''); // Clear input
    setShowCustomInput(false); // Hide input
    setMessageRecommend(''); // Clear any previous message
  };

  // --- Calculate button states --- 
  const canProceedFromRolesAndModels = 
      Object.values(selectedRoles).some(Boolean) && // At least one role selected
      Object.entries(selectedRoles).every(([roleName, isSelected]) => // All selected roles have a model
          !isSelected || (isSelected && selectedRoleModels[roleName] && selectedRoleModels[roleName] !== '')
      );

  // --- 渲染 JSX ---
  return (
    <div className="App">
      <h1>AI 专家决策模拟器</h1>

      {/* === 步骤 1: 输入研究内容 === */}
      {currentStep === 1 && (
        <div className="step-section">
          <h2>步骤 1: 输入研究内容</h2>
          <textarea
            value={researchContent}
            onChange={(e) => setResearchContent(e.target.value)}
            placeholder="在此处粘贴或输入您的研究内容..."
            rows={10}
            style={{ width: '90%', marginBottom: '1rem', padding: '10px', border: '1px solid #ccc', borderRadius: '4px' }}
            disabled={isLoadingRecommend}
          />
          <div>
            <button
              onClick={handleRecommendRoles}
              disabled={isLoadingRecommend || !researchContent.trim()}
              style={{ padding: '10px 20px', cursor: 'pointer' }}
            >
              {isLoadingRecommend ? '分析中...' : '推荐专家角色'}
            </button>
          </div>
          {messageRecommend && (
            <p style={{ marginTop: '1rem', color: messageRecommend.startsWith('错误') ? 'red' : 'green' }}>
              {messageRecommend}
            </p>
          )}
        </div>
      )}

      {/* === 步骤 3: 选择角色和模型 === (合并原步骤 3 和 4) */}
      {currentStep === 3 && (
        <div className="step-section">
          <h2>步骤 3: 选择专家角色和模型</h2>
          <p>勾选角色，并为每个角色选择一个模拟模型引擎:</p>
          {messageRecommend && (
             <p style={{ color: messageRecommend.startsWith('错误') ? 'red' : 'orange' }}>
               {messageRecommend} {/* Display messages like loading models or selection errors */}
             </p>
          )}
          {recommendedRoles.map((roleData, index) => (
            <div key={roleData.role_name + index} style={{ display: 'flex', alignItems: 'center', marginBottom: '1rem', padding: '10px', border: '1px solid #eee', borderRadius: '4px' }}>
              <div style={{ flexBasis: '40%' }}>
                <label title={roleData.score !== null ? `匹配度: ${roleData.score}%` : '自定义角色'}>
                  <input
                    type="checkbox"
                    name={roleData.role_name}
                    checked={selectedRoles[roleData.role_name] || false}
                    onChange={handleRoleCheckboxChange}
                    style={{ marginRight: '8px' }}
                  />
                  {roleData.role_name} 
                  {roleData.score !== null && (
                     <span style={{ fontSize: '0.8em', color: '#555', marginLeft: '5px' }}>({roleData.score}%)</span>
                  )}
                  {roleData.score === null && (
                     <span style={{ fontSize: '0.8em', color: 'blue', marginLeft: '5px' }}>(自定义)</span>
                  )}
                </label>
              </div>
              <div style={{ flexBasis: '50%', marginLeft: '10px' }}>
                {selectedRoles[roleData.role_name] && (
                  <select
                    value={selectedRoleModels[roleData.role_name] || ''}
                    onChange={(e) => handleRoleModelChange(e, roleData.role_name)}
                    disabled={availableModels.length === 0 || isLoadingRecommend}
                    style={{ padding: '5px', width: '100%' }}
                  >
                    {availableModels.length === 0 && <option value="" disabled>加载中...</option>}
                    <option value="" disabled={selectedRoleModels[roleData.role_name] !== ''}>-- 选择模型 --</option>
                    {availableModels.map((model) => (
                      <option key={model.id} value={model.id}>
                        {model.name}
                      </option>
                    ))}
                  </select>
                )}
              </div>
            </div>
          ))}

          {/* --- Add Custom Role Section --- */}
          <div style={{ marginTop: '1.5rem', borderTop: '1px dashed #ccc', paddingTop: '1rem' }}>
            {!showCustomInput ? (
              <button onClick={() => setShowCustomInput(true)} style={{ fontStyle: 'italic' }}>
                + 添加自定义专家
              </button>
            ) : (
              <div>
                <input 
                  type="text"
                  value={customRoleName}
                  onChange={(e) => setCustomRoleName(e.target.value)}
                  placeholder="输入自定义角色名称"
                  style={{ padding: '6px', marginRight: '10px' }}
                />
                <button onClick={handleAddCustomRole} style={{ padding: '6px 12px' }}>确认添加</button>
                <button onClick={() => setShowCustomInput(false)} style={{ marginLeft: '5px', background: '#eee' }}>取消</button>
              </div>
            )}
          </div>

          {/* Navigation Buttons */} 
          <div style={{ marginTop: '1.5rem' }}>
            <button
              onClick={goToPromptDefinition}
              disabled={!canProceedFromRolesAndModels || isLoadingRecommend}
              style={{ padding: '10px 20px', cursor: 'pointer' }}
            >
              下一步: 定义提示词
            </button>
            <button onClick={resetToStep1} style={{ marginLeft: '1rem' }}>返回</button>
          </div>
        </div>
      )}

     {/* === 步骤 4: 定义提示词 === (原步骤 5) */}
      {currentStep === 4 && (
        <div className="step-section">
          <h2>步骤 4: 为选中角色定义提示词</h2>
          <p>为每个选中的专家角色输入具体的指令或问题:</p>
          {Object.entries(selectedRoles)
               .filter(([, isSelected]) => isSelected)
               .map(([roleName], index) => (
                  <div key={index} style={{ marginBottom: '1.5rem', padding: '10px', border: '1px solid #f0f0f0', borderRadius: '4px' }}>
                      <label style={{ fontWeight: 'bold', display: 'block', marginBottom: '5px' }}>
                          {roleName} 的提示词:
                      </label>
                      <textarea
                          name={roleName}
                          value={rolePrompts[roleName] || ''}
                          onChange={handlePromptChange}
                          placeholder="输入此角色的具体任务指令..."
                          rows={3}
                          style={{ width: '85%', marginTop: '5px', padding: '8px', border: '1px solid #ccc', borderRadius: '4px' }}
                          disabled={isLoadingSimulation}
                      />
                  </div>
          ))}
          {simulationMessage && (
            <p style={{ color: 'red' }}>{simulationMessage}</p>
          )}
          <button
            onClick={handleRunSimulation}
            disabled={!canProceedFromRolesAndModels || isLoadingSimulation}
            style={{ padding: '12px 25px', cursor: 'pointer', fontSize: '1.1em', marginTop: '1rem' }}
          >
            {isLoadingSimulation ? '模拟运行中...' : '运行模拟'}
          </button>
           <button onClick={() => setCurrentStep(3)} style={{ marginLeft: '1rem' }}>返回</button>
        </div>
      )}

      {/* === 步骤 6: 显示模拟结果 === (原步骤 7) */}
      {currentStep === 6 && (
         <div className="step-section">
           <h2>步骤 6: 模拟结果</h2>
           {simulationMessage && (
             <p style={{ color: simulationMessage.startsWith('错误') ? 'red' : 'green' }}>{simulationMessage}</p>
           )}
           {Object.entries(simulationResults).map(([roleName, resultText]) => (
             <div key={roleName} style={{ marginBottom: '1.5rem', padding: '15px', border: '1px solid #e0e0e0', borderRadius: '4px', background: '#fdfdfd' }}>
               <h3 style={{ marginTop: 0, color: '#0b6a9c' }}>{roleName} 的回应:</h3>
               <pre style={{ whiteSpace: 'pre-wrap', wordWrap: 'break-word', background: '#f9f9f9', padding: '10px', borderRadius: '3px' }}>
                 {resultText}
               </pre>
             </div>
           ))}
           <button onClick={resetToStep1} style={{ padding: '10px 20px', cursor: 'pointer', marginTop: '1rem' }}>
             重新开始
           </button>
         </div>
      )}

    </div>
  );
}

export default App
